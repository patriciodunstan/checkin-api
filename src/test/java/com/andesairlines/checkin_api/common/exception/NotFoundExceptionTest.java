package com.andesairlines.checkin_api.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotFoundExceptionTest {

    @Test
    void constructor_WithMessage() {
        // Given
        String message = "Resource not found";

        // When
        NotFoundException exception = new NotFoundException(message);

        // Then
        assertEquals("Resource not found", exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void constructor_WithNullMessage() {
        // When
        NotFoundException exception = new NotFoundException(null);

        // Then
        assertNull(exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void constructor_WithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        NotFoundException exception = new NotFoundException(emptyMessage);

        // Then
        assertEquals("", exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void isApiException() {
        // Given
        NotFoundException exception = new NotFoundException("Test message");

        // Then
        assertTrue(exception instanceof ApiException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void errorCodeIsConstant() {
        // Given
        NotFoundException exception1 = new NotFoundException("Message 1");
        NotFoundException exception2 = new NotFoundException("Message 2");
        NotFoundException exception3 = new NotFoundException(null);

        // Then
        assertEquals("NOT_FOUND", exception1.getErrorCode());
        assertEquals("NOT_FOUND", exception2.getErrorCode());
        assertEquals("NOT_FOUND", exception3.getErrorCode());
    }

    @Test
    void commonUseCases() {
        // Test common use case messages
        NotFoundException userNotFound = new NotFoundException("User not found with id: 123");
        NotFoundException flightNotFound = new NotFoundException("Flight not found with id: 456");
        NotFoundException seatNotFound = new NotFoundException("Seat not found: 1A");

        assertEquals("User not found with id: 123", userNotFound.getMessage());
        assertEquals("Flight not found with id: 456", flightNotFound.getMessage());
        assertEquals("Seat not found: 1A", seatNotFound.getMessage());

        // All should have the same error code
        assertEquals("NOT_FOUND", userNotFound.getErrorCode());
        assertEquals("NOT_FOUND", flightNotFound.getErrorCode());
        assertEquals("NOT_FOUND", seatNotFound.getErrorCode());
    }

    @Test
    void longMessage() {
        // Given
        String longMessage = "This is a very long error message that describes in detail what resource was not found and provides additional context about the operation that failed and what the user should do next";

        // When
        NotFoundException exception = new NotFoundException(longMessage);

        // Then
        assertEquals(longMessage, exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void messageWithSpecialCharacters() {
        // Given
        String messageWithSpecialChars = "Resource not found: ID='test@example.com', Type=\"user\", Status=404";

        // When
        NotFoundException exception = new NotFoundException(messageWithSpecialChars);

        // Then
        assertEquals(messageWithSpecialChars, exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
    }
}
