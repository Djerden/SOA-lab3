package com.djeno.city_service.persistence.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class Coordinates {

    // Максимальное значение поля: 329
    @NotNull
    @Max(329)
    private Double x;

    // Минимальное значение поля: -663
    @NotNull
    @Min(-663)
    private Long y;
}
