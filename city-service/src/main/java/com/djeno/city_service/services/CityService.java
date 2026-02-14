package com.djeno.city_service.services;

import com.djeno.city_service.exceptions.CityNotFoundException;
import com.djeno.city_service.persistence.dto.*;
import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.enums.StandardOfLiving;
import com.djeno.city_service.persistence.models.City;
import com.djeno.city_service.persistence.repositories.CityRepository;
import com.djeno.city_service.persistence.specifications.CitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityFilterResponse filterCities(CityFilterRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Проверяем, есть ли сортировка по standardOfLiving
        boolean hasStandardOfLivingSort = request.getSort() != null && 
                request.getSort().stream().anyMatch(s -> "standardOfLiving".equals(s.getField()));
        
        // Создаем сортировку (без standardOfLiving, он обрабатывается в Specification)
        Sort sort = buildSort(request.getSort(), hasStandardOfLivingSort);
        
        // Создаем пагинацию (page в запросе начинается с 1, а в Spring Data с 0)
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        
        // Создаем спецификацию для фильтрации (передаём sortRules для кастомной сортировки)
        CitySpecification specification = new CitySpecification(request.getFilters(), request.getSort());
        
        // Выполняем запрос
        Page<City> cityPage = cityRepository.findAll(specification, pageable);
        
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime);
        
        return CityFilterResponse.builder()
                .page(request.getPage())
                .size(request.getSize())
                .totalItems(cityPage.getTotalElements())
                .totalPages(cityPage.getTotalPages())
                .hasNext(cityPage.hasNext())
                .hasPrevious(cityPage.hasPrevious())
                .sort(request.getSort())
                .filters(request.getFilters())
                .elapsedTime(elapsedTime)
                .timestamp(Instant.now())
                .items(cityPage.getContent())
                .build();
    }
    
    private Sort buildSort(List<SortRule> sortRules, boolean excludeStandardOfLiving) {
        if (sortRules == null || sortRules.isEmpty()) {
            return Sort.unsorted();
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        for (SortRule rule : sortRules) {
            String field = rule.getField();
            
            // Пропускаем standardOfLiving, он обрабатывается в Specification через CASE WHEN
            if (excludeStandardOfLiving && "standardOfLiving".equals(field)) {
                continue;
            }
            
            Sort.Direction direction = "desc".equalsIgnoreCase(rule.getDirection()) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, field));
        }
        
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    @Transactional
    public City createCity(CreateCityRequest request) {
        City city = request.toCity();
        return cityRepository.save(city);
    }

    public City getCity(int id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + id + "' not found"));
    }

    @Transactional
    public void updateCity(int id, UpdateCityRequest request) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + id + "' not found"));

        request.applyTo(city);
        cityRepository.save(city);
    }

    @Transactional
    public void deleteCity(int id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + id + "' not found"));
        cityRepository.delete(city);
    }

    @Transactional
    public int deleteCityByGovernorAge(long age) {
        return cityRepository.deleteByGovernorAge(age);
    }

    public City getCityWithMinCoordinates() {
        return cityRepository.findFirstByOrderByCoordinates_XAscCoordinates_YAsc()
                .orElseThrow(() -> new CityNotFoundException("City with min coordinates not found"));
    }

    public int countCitiesByClimate(Climate climate) {
        return cityRepository.countByClimate(climate);
    }
    
    // Получить город с самым низким уровнем жизни, но не выше указанного уровня
    public City getPoorestCity(Integer excludeId, StandardOfLiving maxStandardOfLiving) {
        int maxLevel = standardOfLivingToLevel(maxStandardOfLiving);
        int excludeIdValue = excludeId != null ? excludeId : -1;
        
        return cityRepository.findPoorestCityWithMaxLevel(excludeIdValue, maxLevel)
                .orElseThrow(() -> new CityNotFoundException("No suitable city found for relocation"));
    }
    
    private int standardOfLivingToLevel(StandardOfLiving sol) {
        if (sol == null) return 2; // HIGH по умолчанию (любой город подходит)
        return switch (sol) {
            case ULTRA_LOW -> 0;
            case VERY_LOW -> 1;
            case HIGH -> 2;
        };
    }
    
    // Уничтожить население города (установить население в 0)
    @Transactional
    public void killPopulation(int id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + id + "' not found"));
        city.setPopulation(0L);
        cityRepository.save(city);
    }
    
    // Переселить население из одного города в другой
    @Transactional
    public void relocatePopulation(int fromCityId, int toCityId) {
        City fromCity = cityRepository.findById(fromCityId)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + fromCityId + "' not found"));
        City toCity = cityRepository.findById(toCityId)
                .orElseThrow(() -> new CityNotFoundException("City with id '" + toCityId + "' not found"));
        
        toCity.setPopulation(toCity.getPopulation() + fromCity.getPopulation());
        fromCity.setPopulation(0L);
        
        cityRepository.save(fromCity);
        cityRepository.save(toCity);
    }
}
