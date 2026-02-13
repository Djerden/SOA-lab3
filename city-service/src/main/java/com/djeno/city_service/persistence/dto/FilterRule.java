package com.djeno.city_service.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRule {
    private String field;
    private String operator = "eq";
    private String value;
}
