package com.djeno.genocide_service.exceptions;

public class CityNotFoundException extends RuntimeException {
    
    public CityNotFoundException(String message) {
        super(message);
    }
}
