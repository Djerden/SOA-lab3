package com.djeno.genocide_service.ejb;

import jakarta.ejb.Remote;

/**
 * Remote интерфейс для EJB-компонента GenocideService.
 * Обеспечивает удалённый доступ к бизнес-логике.
 */
@Remote
public interface GenocideServiceRemote {

    /**
     * Уничтожить население города.
     * @param cityId ID города
     * @throws CityNotFoundException если город не найден
     */
    void killPopulationOfCity(int cityId) throws CityNotFoundException;

    /**
     * Переселить население в самый бедный город.
     * @param cityId ID исходного города
     * @return ID города, в который переселено население
     * @throws CityNotFoundException если город не найден
     */
    int movePopulationToPoorestCity(int cityId) throws CityNotFoundException;
}
