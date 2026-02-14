package com.djeno.city_service.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.djeno.city_service.persistence.enums.Climate;
import com.djeno.city_service.persistence.models.City;

@Repository
public interface CityRepository extends JpaRepository<City, Integer>, JpaSpecificationExecutor<City> {

    @Modifying
    @Transactional
    int deleteByGovernorAge(long age);

    Optional<City> findFirstByOrderByCoordinates_XAscCoordinates_YAsc();

    int countByClimate(Climate climate);
    
    // Найти город с самым низким уровнем жизни, но не выше чем у исходного города
    // maxLevel: 0=ULTRA_LOW, 1=VERY_LOW, 2=HIGH
    @Query("SELECT c FROM City c WHERE c.id != :excludeId AND " +
           "CASE c.standardOfLiving " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.ULTRA_LOW THEN 0 " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.VERY_LOW THEN 1 " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.HIGH THEN 2 " +
           "END <= :maxLevel " +
           "ORDER BY CASE c.standardOfLiving " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.ULTRA_LOW THEN 0 " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.VERY_LOW THEN 1 " +
           "WHEN com.djeno.city_service.persistence.enums.StandardOfLiving.HIGH THEN 2 " +
           "END ASC LIMIT 1")
    Optional<City> findPoorestCityWithMaxLevel(@Param("excludeId") int excludeId, @Param("maxLevel") int maxLevel);
}
