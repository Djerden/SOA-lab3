package com.djeno.city_service.persistence.dto;

import com.djeno.city_service.persistence.models.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityFilterResponse {
    
    private Integer page;
    private Integer size;
    private Long totalItems;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<SortRule> sort;
    private List<FilterRule> filters;
    private Double elapsedTime;
    private Instant timestamp;
    private List<City> items;
}
