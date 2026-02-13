package com.djeno.city_service.persistence.dto;

import com.djeno.city_service.persistence.enums.Climate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClimateCountRequest {
    
    @NotNull
    private Climate climate;
}
