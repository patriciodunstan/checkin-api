package com.andesairlines.checkin_api.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BadRequestExceptionTest {

    @Test
    void constructor_WithMessage() {
        // Given
        String message = "Invalid request parameters";

        // When
        BadRequestException exception = new BadRequestException(message);

        // Then
        assertEquals("Invalid request parameters", exception.getMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode());
    }

    @Test
    void constructor_WithNullMessage() {
        // When
        BadRequestException exception = new BadRequestException(null);

        // Then
        assertNull(exception.getMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode());
    }

    @Test
    void constructor_WithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        BadRequestException exception = new BadRequestException(emptyMessage);

        // Then
        assertEquals("", exception.getMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode());
    }

    @Test
    void isApiException() {
        // Given
        BadRequestException exception = new BadRequestException("Test message");

        // Then
        assertTrue(exception instanceof ApiException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void errorCodeIsConstant() {
        // Given
        BadRequestException exception1 = new BadRequestException("Message 1");
        BadRequestException exception2 = new BadRequestException("Message 2");
        BadRequestException exception3 = new BadRequestException(null);

        // Then
        assertEquals("BAD_REQUEST", exception1.getErrorCode());
        assertEquals("BAD_REQUEST", exception2.getErrorCode());
        assertEquals("BAD_REQUEST", exception3.getErrorCode());
    }
}
