package com.djeno.genocide_service.ejb.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO для города.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private CoordinatesDTO coordinates;
    private LocalDateTime creationDate;
    private long area;
    private long population;
    private Double metersAboveSeaLevel;
    private boolean capital;
    private String climate;
    private String standardOfLiving;
    private HumanDTO governor;
}
