package com.andesairlines.checkin_api.common.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    @Test
    void constructor_AllArgsConstructor() {
        // Given
        Integer code = 200;
        String data = "test data";
        String errors = "test error";

        // When
        ApiResponse<String> response = new ApiResponse<>(code, data, errors);

        // Then
        assertEquals(200, response.getCode());
        assertEquals("test data", response.getData());
        assertEquals("test error", response.getErrors());
    }

    @Test
    void constructor_NoArgsConstructor() {
        // When
        ApiResponse<String> response = new ApiResponse<>();

        // Then
        assertNull(response.getCode());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    void success_StaticMethod() {
        // Given
        String testData = "success data";

        // When
        ApiResponse<String> response = ApiResponse.success(testData);

        // Then
        assertEquals(200, response.getCode());
        assertEquals("success data", response.getData());
        assertNull(response.getErrors());
    }

    @Test
    void success_WithNullData() {
        // When
        ApiResponse<String> response = ApiResponse.success(null);

        // Then
        assertEquals(200, response.getCode());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    void error_StaticMethod() {
        // Given
        Integer errorCode = 400;
        String errorMessage = "Bad request";

        // When
        ApiResponse<String> response = ApiResponse.error(errorCode, errorMessage);

        // Then
        assertEquals(400, response.getCode());
        assertNull(response.getData());
        assertEquals("Bad request", response.getErrors());
    }

    @Test
    void error_WithNullErrorMessage() {
        // Given
        Integer errorCode = 500;

        // When
        ApiResponse<String> response = ApiResponse.error(errorCode, null);

        // Then
        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    void settersAndGetters() {
        // Given
        ApiResponse<Integer> response = new ApiResponse<>();

        // When
        response.setCode(201);
        response.setData(42);
        response.setErrors("validation error");

        // Then
        assertEquals(201, response.getCode());
        assertEquals(42, response.getData());
        assertEquals("validation error", response.getErrors());
    }

    @Test
    void success_WithComplexObject() {
        // Given
        TestObject testObject = new TestObject("test", 123);

        // When
        ApiResponse<TestObject> response = ApiResponse.success(testObject);

        // Then
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("test", response.getData().getName());
        assertEquals(123, response.getData().getValue());
        assertNull(response.getErrors());
    }

    @Test
    void equals_AndHashCode() {
        // Given
        ApiResponse<String> response1 = new ApiResponse<>(200, "data", null);
        ApiResponse<String> response2 = new ApiResponse<>(200, "data", null);
        ApiResponse<String> response3 = new ApiResponse<>(400, "data", "error");

        // Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void toString_Method() {
        // Given
        ApiResponse<String> response = new ApiResponse<>(200, "test", null);

        // When
        String result = response.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("200"));
        assertTrue(result.contains("test"));
    }

    // Helper class for testing complex objects
    private static class TestObject {
        private String name;
        private Integer value;

        public TestObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public Integer getValue() { return value; }
    }
}
