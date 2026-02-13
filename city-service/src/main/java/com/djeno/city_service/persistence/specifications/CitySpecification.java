package com.djeno.city_service.persistence.specifications;

import com.djeno.city_service.persistence.dto.FilterRule;
import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.enums.StandardOfLiving;
import com.djeno.city_service.persistence.models.City;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CitySpecification implements Specification<City> {

    private final List<FilterRule> filters;

    public CitySpecification(List<FilterRule> filters) {
        this.filters = filters != null ? filters : new ArrayList<>();
    }

    @Override
    public Predicate toPredicate(Root<City> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        for (FilterRule filter : filters) {
            String field = filter.getField();
            String operator = filter.getOperator();
            String value = filter.getValue();

            if (field == null || value == null) {
                continue;
            }

            switch (field) {
                case "name" -> predicates.add(buildStringPredicate(root, cb, field, operator, value));
                case "population", "area" -> predicates.add(buildNumericPredicate(root, cb, field, operator, Long.parseLong(value)));
                case "capital" -> predicates.add(cb.equal(root.get(field), Boolean.parseBoolean(value)));
                case "climate" -> {
                    Climate climate = Climate.valueOf(value);
                    predicates.add(cb.equal(root.get(field), climate));
                }
                case "standardOfLiving" -> {
                    StandardOfLiving sol = StandardOfLiving.valueOf(value);
                    predicates.add(buildStandardOfLivingPredicate(root, cb, operator, sol));
                }
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildStringPredicate(Root<City> root, CriteriaBuilder cb, String field, String operator, String value) {
        return switch (operator) {
            case "eq" -> cb.equal(root.get(field), value);
            case "ne" -> cb.notEqual(root.get(field), value);
            default -> cb.equal(root.get(field), value);
        };
    }

    private Predicate buildNumericPredicate(Root<City> root, CriteriaBuilder cb, String field, String operator, Long value) {
        return switch (operator) {
            case "eq" -> cb.equal(root.get(field), value);
            case "gt" -> cb.greaterThan(root.get(field), value);
            case "lt" -> cb.lessThan(root.get(field), value);
            case "gte" -> cb.greaterThanOrEqualTo(root.get(field), value);
            case "lte" -> cb.lessThanOrEqualTo(root.get(field), value);
            case "ne" -> cb.notEqual(root.get(field), value);
            default -> cb.equal(root.get(field), value);
        };
    }

    private Predicate buildStandardOfLivingPredicate(Root<City> root, CriteriaBuilder cb, String operator, StandardOfLiving value) {
        // StandardOfLiving: HIGH(0) > VERY_LOW(1) > ULTRA_LOW(2) - ordinal от лучшего к худшему
        return switch (operator) {
            case "eq" -> cb.equal(root.get("standardOfLiving"), value);
            case "ne" -> cb.notEqual(root.get("standardOfLiving"), value);
            case "gt" -> cb.lessThan(root.get("standardOfLiving"), value); // better than
            case "lt" -> cb.greaterThan(root.get("standardOfLiving"), value); // worse than
            case "gte" -> cb.lessThanOrEqualTo(root.get("standardOfLiving"), value);
            case "lte" -> cb.greaterThanOrEqualTo(root.get("standardOfLiving"), value);
            default -> cb.equal(root.get("standardOfLiving"), value);
        };
    }
}
