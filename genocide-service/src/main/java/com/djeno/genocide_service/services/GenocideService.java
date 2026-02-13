package com.djeno.genocide_service.services;

import com.djeno.genocide_service.exceptions.CityNotFoundException;
import com.djeno.genocide_service.persistence.dto.CityDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GenocideService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GenocideService(RestTemplate restTemplate, @Value("${city-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Уничтожить население города
     * Вызывает city-service для установки population = 0
     */
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

    /**
     * Переселить население города в город с самым низким уровнем жизни
     * @return id города назначения
     */
    public int movePopulationToPoorestCity(int id) {
        // Получить город с самым низким уровнем жизни
        String poorestUrl = baseUrl + "cities/poorest";
        CityDTO poorestCity;
        try {
            ResponseEntity<CityDTO> response = restTemplate.getForEntity(poorestUrl, CityDTO.class);
            poorestCity = response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException("No cities found to relocate to");
            }
            throw e;
        }

        if (poorestCity == null) {
            throw new CityNotFoundException("No cities found to relocate to");
        }

        // Переселить население
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
