package com.djeno.city_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.djeno.city_service.persistence.dto.CityFilterRequest;
import com.djeno.city_service.persistence.dto.CityFilterResponse;
import com.djeno.city_service.persistence.dto.ClimateCountRequest;
import com.djeno.city_service.persistence.dto.CreateCityRequest;
import com.djeno.city_service.persistence.dto.NumberOfCitiesResponse;
import com.djeno.city_service.persistence.dto.ResponseMessage;
import com.djeno.city_service.persistence.dto.UpdateCityRequest;
import com.djeno.city_service.persistence.enums.StandardOfLiving;
import com.djeno.city_service.persistence.models.City;
import com.djeno.city_service.services.CityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/cities")
@RestController
public class CityController {

    private final CityService cityService;

    @PostMapping("/filter")
    public ResponseEntity<CityFilterResponse> filterCities(@Valid @RequestBody CityFilterRequest request) {
        CityFilterResponse response = cityService.filterCities(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<City> createCity(@Valid @RequestBody CreateCityRequest request) {
        City city = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(city);
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCity(@PathVariable int id) {
        City city = cityService.getCity(id);
        return ResponseEntity.ok(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCity(@PathVariable Integer id, @Valid @RequestBody UpdateCityRequest request) {
        cityService.updateCity(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-governor")
    public ResponseEntity<ResponseMessage> deleteCityByGovernor(@RequestParam Integer age) {
        int countOfCities = cityService.deleteCityByGovernorAge(age);
        return ResponseEntity.ok(new ResponseMessage("Cities with governor age '" + age + "' were deleted. Count: " + countOfCities));
    }

    @GetMapping("/by-min-coordinates")
    public ResponseEntity<City> getCityWithMinCoordinates() {
        City city = cityService.getCityWithMinCoordinates();
        return ResponseEntity.ok(city);
    }

    @PostMapping("/count-by-climate")
    public ResponseEntity<NumberOfCitiesResponse> countCitiesByClimate(@Valid @RequestBody ClimateCountRequest request) {
        int count = cityService.countCitiesByClimate(request.getClimate());
        return ResponseEntity.ok(new NumberOfCitiesResponse(count));
    }

    @GetMapping("/poorest")
    public ResponseEntity<City> getPoorestCity(
            @RequestParam(required = false) Integer excludeId,
            @RequestParam(required = false) StandardOfLiving maxStandardOfLiving) {
        City city = cityService.getPoorestCity(excludeId, maxStandardOfLiving);
        return ResponseEntity.ok(city);
    }

    @PostMapping("/{id}/kill-population")
    public ResponseEntity<Void> killPopulation(@PathVariable int id) {
        cityService.killPopulation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{fromId}/relocate-to/{toId}")
    public ResponseEntity<Void> relocatePopulation(@PathVariable int fromId, @PathVariable int toId) {
        cityService.relocatePopulation(fromId, toId);
        return ResponseEntity.ok().build();
    }
}
