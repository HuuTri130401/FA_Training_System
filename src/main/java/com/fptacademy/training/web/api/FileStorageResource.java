package com.fptacademy.training.web.api;

import com.fptacademy.training.service.dto.FileStorageDto;
import com.fptacademy.training.service.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api")
public interface FileStorageResource {

    @Operation(summary = "Get all file", description = "Get all file", tags = "file storage", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all file successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("{hasAnyAuthority('User_FullAccess'), hasAnyAuthority('User_Modify'),hasAnyAuthority('User_Create')}")
    @GetMapping(value = "/file/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<FileStorageDto>> getAllFiles();

    @Operation(summary = "Upload file to S3 successfully", description = "Upload file to S3 successfully", tags = "file storage", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload file to S3 successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("{hasAnyAuthority('User_FullAccess'), hasAnyAuthority('User_Modify'),hasAnyAuthority('User_Create')}")
    @RequestMapping(value = "/file-upload/{description}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String description);

}
