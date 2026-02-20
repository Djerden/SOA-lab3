package com.djeno.genocide_service.ejb.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * DTO для координат.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer x;
    private int y;
}
