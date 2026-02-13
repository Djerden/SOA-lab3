package com.djeno.city_service.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortRule {
    private String field;
    private String direction = "asc";
}
