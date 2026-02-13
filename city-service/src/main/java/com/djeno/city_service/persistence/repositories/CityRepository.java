package com.djeno.city_service.persistence.repositories;

import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer>, JpaSpecificationExecutor<City> {

    @Modifying
    @Transactional
    int deleteByGovernorAge(long age);

    Optional<City> findFirstByOrderByCoordinates_XAscCoordinates_YAsc();

    int countByClimate(Climate climate);
    
    // Найти город с самым низким уровнем жизни
    Optional<City> findFirstByOrderByStandardOfLivingDesc();
}
