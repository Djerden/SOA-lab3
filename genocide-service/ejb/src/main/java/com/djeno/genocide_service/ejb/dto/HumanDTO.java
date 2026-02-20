package com.djeno.genocide_service.ejb.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * DTO для губернатора.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HumanDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long age;
}
