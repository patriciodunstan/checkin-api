package com.andesairlines.checkin_api.common.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    @Test
    void constructor_AllArgsConstructor() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test error message";
        List<String> details = Arrays.asList("detail1", "detail2");
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ErrorResponse response = new ErrorResponse(errorCode, message, details, timestamp);

        // Then
        assertEquals("TEST_ERROR", response.getErrorCode());
        assertEquals("Test error message", response.getMessage());
        assertEquals(2, response.getDetails().size());
        assertEquals("detail1", response.getDetails().get(0));
        assertEquals("detail2", response.getDetails().get(1));
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void constructor_NoArgsConstructor() {
        // When
        ErrorResponse response = new ErrorResponse();

        // Then
        assertNull(response.getErrorCode());
        assertNull(response.getMessage());
        assertNull(response.getDetails());
        assertNull(response.getTimestamp());
    }

    @Test
    void constructor_TwoArgsConstructor() {
        // Given
        String errorCode = "SIMPLE_ERROR";
        String message = "Simple error message";
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // When
        ErrorResponse response = new ErrorResponse(errorCode, message);

        // Then
        assertEquals("SIMPLE_ERROR", response.getErrorCode());
        assertEquals("Simple error message", response.getMessage());
        assertNull(response.getDetails());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(beforeCreation));
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void constructor_ThreeArgsConstructor() {
        // Given
        String errorCode = "DETAILED_ERROR";
        String message = "Detailed error message";
        List<String> details = Arrays.asList("validation failed", "field is required");
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // When
        ErrorResponse response = new ErrorResponse(errorCode, message, details);

        // Then
        assertEquals("DETAILED_ERROR", response.getErrorCode());
        assertEquals("Detailed error message", response.getMessage());
        assertEquals(2, response.getDetails().size());
        assertEquals("validation failed", response.getDetails().get(0));
        assertEquals("field is required", response.getDetails().get(1));
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(beforeCreation));
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void settersAndGetters() {
        // Given
        ErrorResponse response = new ErrorResponse();
        LocalDateTime testTimestamp = LocalDateTime.now();
        List<String> testDetails = Arrays.asList("setter test");

        // When
        response.setErrorCode("SETTER_ERROR");
        response.setMessage("Setter test message");
        response.setDetails(testDetails);
        response.setTimestamp(testTimestamp);

        // Then
        assertEquals("SETTER_ERROR", response.getErrorCode());
        assertEquals("Setter test message", response.getMessage());
        assertEquals(1, response.getDetails().size());
        assertEquals("setter test", response.getDetails().get(0));
        assertEquals(testTimestamp, response.getTimestamp());
    }
}
