package com.djeno.genocide_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответных сообщений.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
    private String message;
}
