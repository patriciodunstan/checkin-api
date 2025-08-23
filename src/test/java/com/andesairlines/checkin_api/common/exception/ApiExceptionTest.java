package com.andesairlines.checkin_api.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

public class ApiExceptionTest {

    @Test
    void constructor_ThreeArgs() {
        // Given
        String message = "Test API error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorCode = "CUSTOM_ERROR";

        // When
        ApiException exception = new ApiException(message, status, errorCode);

        // Then
        assertEquals("Test API error", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("CUSTOM_ERROR", exception.getErrorCode());
    }

    @Test
    void constructor_TwoArgs_DefaultErrorCode() {
        // Given
        String message = "Test API error with default code";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // When
        ApiException exception = new ApiException(message, status);

        // Then
        assertEquals("Test API error with default code", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("API_ERROR", exception.getErrorCode());
    }

    @Test
    void constructor_NullMessage() {
        // Given
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorCode = "NULL_MESSAGE_ERROR";

        // When
        ApiException exception = new ApiException(null, status, errorCode);

        // Then
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("NULL_MESSAGE_ERROR", exception.getErrorCode());
    }

    @Test
    void isRuntimeException() {
        // Given
        ApiException exception = new ApiException("Test", HttpStatus.BAD_REQUEST, "TEST_ERROR");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void differentHttpStatuses() {
        // Test various HTTP statuses
        ApiException badRequest = new ApiException("Bad request", HttpStatus.BAD_REQUEST);
        ApiException notFound = new ApiException("Not found", HttpStatus.NOT_FOUND);
        ApiException unauthorized = new ApiException("Unauthorized", HttpStatus.UNAUTHORIZED);

        assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatus());

        // All should have default error code
        assertEquals("API_ERROR", badRequest.getErrorCode());
        assertEquals("API_ERROR", notFound.getErrorCode());
        assertEquals("API_ERROR", unauthorized.getErrorCode());
    }
}
