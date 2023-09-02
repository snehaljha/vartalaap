package com.gazab.chatServer.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    private String msg;
    private Throwable throwable;
    private HttpStatus httpStatus;
    private LocalDateTime localDateTime;
    
}
