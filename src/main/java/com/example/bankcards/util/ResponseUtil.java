package com.example.bankcards.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;

@UtilityClass
public class ResponseUtil {

    public void sendResponse(
            ResponseEntity<?> responseEntity, HttpServletResponse response
    ) throws IOException {
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter writer = response.getWriter()) {
            writer.print(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
        }
    }
}
