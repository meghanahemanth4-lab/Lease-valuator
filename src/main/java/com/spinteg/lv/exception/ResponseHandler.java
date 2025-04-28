package com.spinteg.lv.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<?> handleResponseEntity(String message, Object body, String warning) {
        Map<String, Object> response = new HashMap<>();
        if (warning != null)
            response.put("warning", warning);
        response.put("message", message);
        response.put("body", body);
        ResponseEntity<?> responseEntity = ResponseEntity.status(200).body(response);
        return responseEntity;
    }

}
