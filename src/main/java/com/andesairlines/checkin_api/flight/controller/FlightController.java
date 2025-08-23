package com.andesairlines.checkin_api.flight.controller;

import com.andesairlines.checkin_api.common.response.ApiResponse;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;

import com.andesairlines.checkin_api.flight.service.CheckinService;
import com.andesairlines.checkin_api.flight.service.SeatAssignmentService;

import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Flight Management", description = "APIs for flight and seat management")
public class FlightController {

    private final CheckinService checkinService;
    private final SeatAssignmentService seatAssignmentService;

    @GetMapping("/{flightId}/passengers")
    @Operation(summary = "Get flight with passengers", description = "Retrieve flight information including all passengers and their seat assignments")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flight found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flight not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<FlightResponse>> getFlightWithPassengers(
            @Parameter(description = "Flight ID", required = true)
            @PathVariable @NotNull Integer flightId) {

        log.info("GET /flights/{}/passengers - Fetching flight with passengers", flightId);

        FlightResponse flight = checkinService.performCheckin(flightId);
        return ResponseEntity.ok(ApiResponse.success(flight));
    }

    @PutMapping("/{flightId}/passengers/{passengerId}/seat")
    @Operation(summary = "Assign seat to passenger", description = "Assign a specific seat to a passenger on a flight")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seat assigned successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - invalid seat or seat already taken"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flight, passenger, or seat not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<PassengerResponse>> assignSeat(
            @Parameter(description = "Flight ID", required = true)
            @PathVariable @NotNull Integer flightId,

            @Parameter(description = "Passenger ID", required = true)
            @PathVariable @NotNull Integer passengerId,

            @Parameter(description = "Seat row number", required = true)
            @RequestParam @NotNull Integer seatRow,

            @Parameter(description = "Seat column letter (A-F)", required = true)
            @RequestParam @NotNull @Pattern(regexp = "^[A-F]$", message = "Seat column must be a letter between A and F") String seatColumn) {

        log.info("PUT /flights/{}/passengers/{}/seat - Assigning seat {}:{}", flightId, passengerId, seatRow, seatColumn);

        PassengerResponse passenger = seatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn);
        return ResponseEntity.ok(ApiResponse.success( passenger));
    }
}
