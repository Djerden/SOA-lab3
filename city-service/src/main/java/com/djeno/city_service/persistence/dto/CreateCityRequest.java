package com.djeno.city_service.persistence.dto;

import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.enums.StandardOfLiving;
import com.djeno.city_service.persistence.models.City;
import com.djeno.city_service.persistence.models.Coordinates;
import com.djeno.city_service.persistence.models.Human;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCityRequest(
        @NotBlank(message = "Name is required and cannot be blank")
        @NotNull(message = "Name is required")
        String name,
        
        @NotNull(message = "Coordinates are required")
        @Valid
        Coordinates coordinates,
        
        @Min(value = 1, message = "Area must be greater than 0")
        @NotNull(message = "Area is required")
        Long area,
        
        @Min(value = 1, message = "Population must be greater than 0")
        @NotNull(message = "Population is required")
        Long population,
        
        Double metersAboveSeaLevel,
        
        @NotNull(message = "Capital is required")
        Boolean capital,
        
        Climate climate,
        
        @NotNull(message = "Standard of living is required")
        StandardOfLiving standardOfLiving,
        
        @NotNull(message = "Governor is required")
        @Valid
        Human governor
) {
    public City toCity() {
        City city = new City();
        city.setName(this.name());
        city.setCoordinates(this.coordinates());
        city.setArea(this.area());
        city.setPopulation(this.population());
        city.setMetersAboveSeaLevel(this.metersAboveSeaLevel());
        city.setCapital(this.capital());
        city.setClimate(this.climate());
        city.setStandardOfLiving(this.standardOfLiving());
        city.setGovernor(this.governor());
        return city;
    }
}
