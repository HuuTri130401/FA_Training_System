package com.fptacademy.training.web.api;

import com.fptacademy.training.service.dto.ReturnClassScheduleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api")
@EnableMethodSecurity
@SuppressWarnings("unused")
public interface ClassScheduleResource {

    @Operation(
            summary = "Get all opening class schedule by date",
            description = "Login user can view all opening class schedule by date",
            tags = "Class Schedule",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get class schedule successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid request parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class schedule not found", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleByDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) List<String> className,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> city
    );

    @Operation(
            summary = "Get all opening class schedule by week",
            description = "Login user can view all opening class schedule in a week by entering a date in this week",
            tags = "Class Schedule",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get class schedule successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid request parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class schedule not found", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/calendar/week", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleByWeek(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) List<String> className,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> city
    );

    @Operation(
            summary = "Get all opening class schedule of user by date",
            description = "Login user can view their opening class schedule by date",
            tags = "Class Schedule",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get class schedule successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid request parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class schedule not found", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "user/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleOfCurrentUser(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) List<String> className,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> city
    );

    @Operation(
            summary = "Get all opening class schedule of user by week",
            description = "Login user can view all their opening class schedule in a week by entering a date in this week.",
            tags = "Class Schedule",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get class schedule successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid request parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class schedule not found", content = @Content)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "user/calendar/week", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleOfCurrentUserByWeek(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) List<String> className,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> city
    );
}
