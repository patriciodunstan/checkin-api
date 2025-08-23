package com.andesairlines.checkin_api.common.exception;

import com.andesairlines.checkin_api.common.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleApiException_Success() {
        // Given
        ApiException apiException = new ApiException("Test API error", HttpStatus.BAD_REQUEST, "API_ERROR");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleApiException(apiException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("API_ERROR", response.getBody().getErrorCode());
        assertEquals("Test API error", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException_Success() {
        // Given
        NotFoundException notFoundException = new NotFoundException("Resource not found");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(notFoundException);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleBadRequestException_Success() {
        // Given
        BadRequestException badRequestException = new BadRequestException("Invalid request");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestException(badRequestException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getErrorCode());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_Success() {
        // Given
        Exception genericException = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(genericException);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}
