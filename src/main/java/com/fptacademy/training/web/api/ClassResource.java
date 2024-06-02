package com.fptacademy.training.web.api;

import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.ClassDetail;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.service.dto.*;
import com.fptacademy.training.web.vm.ClassListResponseVM;
import com.fptacademy.training.web.vm.ClassVM;
import com.fptacademy.training.web.vm.ProgramVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequestMapping("/api/class")
public interface ClassResource {

    @Operation(
            summary = "Get a class by class ID",
            description = "Get a class by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find a class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Class ID not found", content = @Content)
    })
    @GetMapping("/{class_id}")
    public ResponseEntity<ClassDto> getClassById(@PathVariable Long class_id);

    @Operation(
            summary = "Get details of a class by class ID",
            description = "Get details of a class by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find details of a class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Class ID not found", content = @Content)
    })
    @GetMapping("/details/{class_id}")
    public ResponseEntity<ClassDetailDto> getDetailsByClassId(@PathVariable Long class_id);

    @Operation(
            summary = "Get list of classes (can be filtered) by class ID",
            description = "Get list of classes (can be filtered) by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find list of class successfully"),
    })
    @GetMapping
    public ResponseEntity<ClassListResponseVM> filterClass(
            @Parameter(description = "Input any keywords for searching the list of classes")
            @RequestParam(value = "keywords", required = false) List<String> keywords,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "location", required = false) List<String> cities,
            @RequestParam(name = "classTime", required = false) List<String> classTimes,
            @RequestParam(name = "status", required = false) List<String> statuses,
            @RequestParam(name = "attendee", required = false) List<String> attendeeTypes,
            @RequestParam(name = "fsu", required = false) String fsu,
            @RequestParam(name = "trainer", required = false) String trainerCode,
            @Parameter(
                    description = "Sort string in the format: property,(asc|desc), can just sort with one " +
                    "criteria. " +
                    "Property in sort string includes: \"id\", \"name\", \"code\", \"createdOn\", \"createdBy\", " +
                    "\"duration\", \"attendee\", \"location\", \"fsu\""
            )
            @RequestParam(value = "sort", required = false, defaultValue = "id,asc") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    );

    @Operation(
            summary = "Get list of classes (can be filtered) by class ID (version 2)",
            description = "Get list of classes (can be filtered) by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find list of class successfully"),
    })
    @GetMapping("/all-v2")
    public ResponseEntity<Page<ClassDto>> filterClassV2(
            @Parameter(description = "Input any keywords for searching the list of classes")
            @RequestParam(value = "keywords", required = false) List<String> keywords,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "location", required = false) List<String> cities,
            @RequestParam(name = "classTime", required = false) List<String> classTimes,
            @RequestParam(name = "status", required = false) List<String> statuses,
            @RequestParam(name = "attendee", required = false) List<String> attendeeTypes,
            @RequestParam(name = "fsu", required = false) String fsu,
            @RequestParam(name = "trainer", required = false) String trainerCode,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Delete a class by class ID",
            description = "Delete a class by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find a class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Class ID not found", content = @Content)
    })
    @DeleteMapping ("/{id}")
    public ResponseEntity<String> delClass(@PathVariable Long id);

    @Operation(
            summary = "Deactivate a class by class ID",
            description = "Deactivate a class by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find a class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Class ID not found", content = @Content)
    })
    @PutMapping ("/deactivate/{id}")
    public ResponseEntity<String> deactivateClass(@PathVariable Long id);

    @Operation(
            summary = "Activate a class by class ID",
            description = "Activate a class by class ID",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find a class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "Class ID not found", content = @Content)
    })
    @PutMapping ("/activate/{id}")
    public ResponseEntity<String> activateClass(@PathVariable Long id);

    @Operation(
            summary = "Create a new class",
            description = "Create a new class (can be saved as draft by set the status field to 'DRAFT')",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "201", description = "Create a new class successfully"),
            @ApiResponse(responseCode = "404", description = "ID not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict class", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ClassDetailDto> createClass(@RequestBody ClassVM classVM);

    @Operation(
            summary = "Get all trainers available for class",
            description = "Get all trainers available for class",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find all trainers successfully")
    })
    @GetMapping("/trainers")
    public ResponseEntity<List<UserDto>> getAllTrainer();

    @Operation(
            summary = "Get all class admins available for class",
            description = "Get all class admins available for class",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find all class admins successfully")
    })
    @GetMapping("/class_admins")
    public ResponseEntity<List<UserDto>> getAllClassAdmin();

    @Operation(
            summary = "Get all attendees types for class",
            description = "Get all attendees types for class",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find all attendees successfully")
    })
    @GetMapping("/attendees")
    public ResponseEntity<List<AttendeeDto>> getAllAttendees();
    @Operation(
            summary = "Get all locations (cities) for class",
            description = "Get all locations (cities) for class",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find locations (cities) successfully")
    })
    @GetMapping("/locations")
    public ResponseEntity<List<LocationDto>> getAllLocations();

    @Operation(
            summary = "Get list of class details by date",
            description = "Get list of class details by date",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Find list of class details by study date successfully")
    })
    @GetMapping("/details")
    public ResponseEntity<List<ClassDetailDto>> getAllClassesDetailsByStudyDate (
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(name = "studyDate", required = true) LocalDate date);

    @Operation(
            summary = "Update an existing class",
            description = "Update an existing class",
            tags = "class",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "200", description = "Update class by ID successfully"),
            @ApiResponse(responseCode = "404", description = "ID not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict class", content = @Content)
    })
    @PutMapping("/{class_id}")
    public ResponseEntity<ClassDetailDto> updateClass(
            @PathVariable Long class_id,
            @RequestBody ClassVM classVM);
}
