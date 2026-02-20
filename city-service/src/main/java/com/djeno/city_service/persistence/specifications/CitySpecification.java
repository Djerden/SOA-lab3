package com.djeno.city_service.persistence.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.djeno.city_service.persistence.dto.FilterRule;
import com.djeno.city_service.persistence.dto.SortRule;
import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.enums.StandardOfLiving;
import com.djeno.city_service.persistence.models.City;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CitySpecification implements Specification<City> {

    private final List<FilterRule> filters;
    private final List<SortRule> sortRules;

    public CitySpecification(List<FilterRule> filters) {
        this(filters, null);
    }

    public CitySpecification(List<FilterRule> filters, List<SortRule> sortRules) {
        this.filters = filters != null ? filters : new ArrayList<>();
        this.sortRules = sortRules != null ? sortRules : new ArrayList<>();
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

        if (!sortRules.isEmpty() && query.getResultType() != Long.class && query.getResultType() != long.class) {
            List<Order> orders = new ArrayList<>();
            for (SortRule sortRule : sortRules) {
                if ("standardOfLiving".equals(sortRule.getField())) {
                    Expression<Integer> solOrder = cb.<Integer>selectCase()
                            .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.HIGH), 0)
                            .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.VERY_LOW), 1)
                            .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.ULTRA_LOW), 2)
                            .otherwise(3);
                    
                    if ("desc".equalsIgnoreCase(sortRule.getDirection())) {
                        orders.add(cb.desc(solOrder));
                    } else {
                        orders.add(cb.asc(solOrder));
                    }
                } else {
                    if ("desc".equalsIgnoreCase(sortRule.getDirection())) {
                        orders.add(cb.desc(root.get(sortRule.getField())));
                    } else {
                        orders.add(cb.asc(root.get(sortRule.getField())));
                    }
                }
            }
            if (!orders.isEmpty()) {
                query.orderBy(orders);
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Expression<Integer> getStandardOfLivingOrder(Root<City> root, CriteriaBuilder cb) {
        return cb.<Integer>selectCase()
                .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.HIGH), 0)
                .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.VERY_LOW), 1)
                .when(cb.equal(root.get("standardOfLiving"), StandardOfLiving.ULTRA_LOW), 2)
                .otherwise(3);
    }

    private Predicate buildStringPredicate(Root<City> root, CriteriaBuilder cb, String field, String operator, String value) {
        return switch (operator) {
            case "eq" -> cb.equal(root.get(field), value);
            case "ne" -> cb.notEqual(root.get(field), value);
            case "contains" -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
            default -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"); // по умолчанию частичное совпадение
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
        return switch (operator) {
            case "eq" -> cb.equal(root.get("standardOfLiving"), value);
            case "ne" -> cb.notEqual(root.get("standardOfLiving"), value);
            case "gt" -> cb.lessThan(root.get("standardOfLiving"), value);
            case "lt" -> cb.greaterThan(root.get("standardOfLiving"), value); 
            case "gte" -> cb.lessThanOrEqualTo(root.get("standardOfLiving"), value);
            case "lte" -> cb.greaterThanOrEqualTo(root.get("standardOfLiving"), value);
            default -> cb.equal(root.get("standardOfLiving"), value);
        };
    }
}
