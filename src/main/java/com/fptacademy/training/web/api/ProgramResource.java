package com.fptacademy.training.web.api;

import com.fptacademy.training.service.dto.ProgramDto;
import com.fptacademy.training.service.dto.SyllabusDto;
import com.fptacademy.training.web.vm.ProgramExcelImportResponseVM;
import com.fptacademy.training.web.vm.ProgramListResponseVM;
import com.fptacademy.training.web.vm.ProgramVM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api")
public interface ProgramResource {
    @Operation(
            summary = "Create a training program",
            description = "Create a training program",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created training program successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict training program name", content = @Content)
    })
    @PostMapping(value = "/programs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramDto> createProgram(@RequestBody ProgramVM programVM);

    //Activate the Program by id
    @Operation(
            summary = "Activate program",
            description = "Activate the program by id ",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activated training program successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @PostMapping(value = "/programs/{id}/activate",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramDto> activateProgram(@PathVariable Long id);

    @Operation(
            summary = "Get list of training programs",
            description = "Get list of training programs with sort and pagination",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found training programs"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/programs", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramListResponseVM> getPrograms(
            @RequestParam(value = "q", required = false) List<String> keywords,
            @RequestParam(value = "activated", required = false) Boolean activated,
            @RequestParam(value = "sort", required = false, defaultValue = "id,asc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size);

    @Operation(
            summary = "Get list of syllabuses details by program id",
            description = "Get list of syllabuses details by program id",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found syllabuses"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Program id not found", content = @Content),
    })
    @GetMapping(value = "/programs/{id}/syllabus", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<SyllabusDto.SyllabusListDto>> getSyllabusesByProgramId(
            @PathVariable Long id
    );

     @Operation(
             summary = "Get specific program by program id",
             description = "Get specific program by program id",
             tags = "program",
             security = @SecurityRequirement(name = "token_auth")
     )
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "Found program"),
             @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
             @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
             @ApiResponse(responseCode = "404", description = "Program id not found", content = @Content),
     })
     @GetMapping(value = "/programs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
     ResponseEntity<ProgramDto> getProgramById(
             @PathVariable Long id
     );

    @Operation(
            summary = "Download excel template for importing training programs",
            description = "Download excel template for importing training programs",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @GetMapping(value = "/programs/import/template", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<Resource> downloadExcelTemplate();

    @Operation(
            summary = "Create a training program by importing excel file",
            description = "Create a training program by importing excel file",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created training program successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict training program", content = @Content)
    })
    @PostMapping(value = "/programs/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramExcelImportResponseVM> importProgramsFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "duplicate", defaultValue = "id") String[] properties,
            @RequestParam(value = "handle", defaultValue = "skip") String handler);

    @Operation(
            summary = "Deactivate program",
            description = "Deactivate program by id",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deactivate training program successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Program not found", content = @Content),
    })
    @PostMapping(value = "/programs/{id}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramDto> deactivateProgram(
            @PathVariable Long id
    );


    @Operation(
            summary = "Update specific training programs",
            description = "Update some attributes of a specific training program: name, syllabuses, syllabuses index ",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found training programs"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @PutMapping(value = "/programs/{id}" ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProgramDto> updateProgram(@RequestBody ProgramVM programVM, @PathVariable(value = "id") final Long id);

    @Operation(
            summary = "Delete training program by id",
            description = "Delete a training program given its ID. A program can only be deleted if there are no classes associated with it.",
            tags = "program",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted training program successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "Program not found", content = @Content),
    })
    @DeleteMapping("/programs/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deleteProgram(@PathVariable Long id);
}
