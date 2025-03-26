package com.spinteg.lv.ExceptionHandling;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<?> handleResponseEntity(String message, Object body) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("body", body.toString());
        ResponseEntity<?> responseEntity = ResponseEntity.status(200).body(response);
        return responseEntity;
    }
}
