package com.djeno.city_service.persistence.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityFilterRequest {
    
    @Min(1)
    private Integer page = 1;
    
    @Min(1)
    @Max(100)
    private Integer size = 10;
    
    private List<SortRule> sort = new ArrayList<>();
    
    private List<FilterRule> filters = new ArrayList<>();
}
