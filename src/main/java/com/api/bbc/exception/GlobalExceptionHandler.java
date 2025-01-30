package com.api.bbc.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Object> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("codigo", "g104");
        errorResponse.put("error", "Faltan las cabeceras X-Api-Key o X-Firma"); // Mensaje más descriptivo
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiKeyInvalidaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Object> handleApiKeyInvalida(ApiKeyInvalidaException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("codigo", "g103"); // Código diferente para API Key inválida
        errorResponse.put("error", ex.getMessage()); // Mensaje de la excepción
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(QueryInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleQueryInvalid(QueryInvalidException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("codigo", "g268"); // Código diferente para query inválida
        errorResponse.put("error", ex.getMessage()); // Mensaje de la excepción
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

    @ExceptionHandler(NoticeNullException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<Object> handleNoticeNullException(NoticeNullException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("codigo", "g404"); // Código diferente para noticia no encontrada
        errorResponse.put("error", ex.getMessage()); // Mensaje de la excepción
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternarlServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Object> handleInternarlServerError(InternarlServerError ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("codigo", "g100"); // Código diferente para error interno
        errorResponse.put("error", ex.getMessage()); // Mensaje de la excepción
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
