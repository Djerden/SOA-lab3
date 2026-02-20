package com.djeno.genocide_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.djeno.genocide_service.exceptions.CityNotFoundException;
import com.djeno.genocide_service.persistence.dto.CityDTO;

@Service
public class GenocideService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GenocideService(RestTemplate restTemplate, @Value("${city-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void killPopulationOfCity(int id) {
        String url = baseUrl + "cities/" + id + "/kill-population";
        try {
            restTemplate.postForEntity(url, null, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException("City with id '" + id + "' not found");
            }
            throw e;
        }
    }

    public int movePopulationToPoorestCity(int id) {
        String sourceUrl = baseUrl + "cities/" + id;
        CityDTO sourceCity;
        try {
            ResponseEntity<CityDTO> sourceResponse = restTemplate.getForEntity(sourceUrl, CityDTO.class);
            sourceCity = sourceResponse.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException("City with id '" + id + "' not found");
            }
            throw e;
        }
        
        if (sourceCity == null) {
            throw new CityNotFoundException("City with id '" + id + "' not found");
        }
        
        String poorestUrl = baseUrl + "cities/poorest?excludeId=" + id + 
                            "&maxStandardOfLiving=" + sourceCity.getStandardOfLiving();
        CityDTO poorestCity;
        try {
            ResponseEntity<CityDTO> response = restTemplate.getForEntity(poorestUrl, CityDTO.class);
            poorestCity = response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return id;
            }
            throw e;
        }

        if (poorestCity == null) {
            return id;
        }

        String relocateUrl = baseUrl + "cities/" + id + "/relocate-to/" + poorestCity.getId();
        try {
            restTemplate.postForEntity(relocateUrl, null, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException("City with id '" + id + "' not found");
            }
            throw e;
        }
        
        return poorestCity.getId();
    }
}
