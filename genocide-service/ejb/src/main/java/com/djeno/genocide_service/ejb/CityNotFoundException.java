package com.djeno.genocide_service.ejb;

import jakarta.ejb.ApplicationException;

/**
 * Исключение, выбрасываемое когда город не найден.
 */
@ApplicationException(rollback = true)
public class CityNotFoundException extends Exception {

    public CityNotFoundException(String message) {
        super(message);
    }

    public CityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
