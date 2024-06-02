package com.fptacademy.training.web.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fptacademy.training.web.vm.NoNullRequiredUserVM;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fptacademy.training.service.dto.UserDto;
import com.fptacademy.training.service.dto.ReturnPageDto;
import com.fptacademy.training.web.vm.UserVM;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequestMapping("/api")
public interface UserResource {
    @Operation(summary = "Create a user", description = "Create a user", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created user successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict user name", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('User_FullAccess')")
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> createUser(@RequestBody @Valid UserVM userVM);

    ResponseEntity<UserDto> deActiveUser();

    @Operation(summary = "Get users by filters", description = "Get users by filters", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ReturnPageDto<List<UserDto>>> getUsers(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "name", required = false) String fullName,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "level", required = false) String levelName,
            @RequestParam(name = "role", required = false) String roleName,
            @RequestParam(name = "birthdayFrom", required = false) String birthdayFrom,
            @RequestParam(name = "birthdayTo", required = false) String birthdayTo,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sort", required = false, defaultValue = "id,asc") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer pageSize);

    @Operation(summary = "Get user by email", description = "Get user by email", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/users/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('User_FullAccess')")
    ResponseEntity<Optional<UserDto>> getUserByEmail(@PathVariable String email);

    @Operation(summary = "Import users from excel", description = "Import users from excel", tags = "user", security = @SecurityRequirement(name = "token_auth"))

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/import/{description}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> importUsersFromExcel(@RequestParam("file") MultipartFile file, @PathVariable String description);

    @Operation(summary = "Get user by name", description = "Get user by name", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/users/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDto>> getUserByName(@PathVariable String name);

    @Operation(summary = "Change role", description = "Change role", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changeRole(@PathVariable long id, String typeRole);

    @Operation(summary = "Delete user", description = "Delete user by id (change user's activated)", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successfully"),
            @ApiResponse(responseCode = "400", description = "Can't delete your own account"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('User_FullAccess')")
    @DeleteMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> deleteUser(@PathVariable("id") Long id);

    @Operation(summary = "De-active user", description = "De-active user by id (change user's status)", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "De-active user successfully"),
            @ApiResponse(responseCode = "400", description = "Can't delete your own account"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content),
    })
    @PreAuthorize("hasAnyAuthority('User_FullAccess')")
    @PutMapping(value = "/users/{id}/deActive", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> deActive(@PathVariable("id") Long id);

    @Operation(summary = "Get user by id", description = "Get user with user id", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserById(@PathVariable(value = "id") Long id);

    @Operation(summary = "Update user", description = "Update user field is changed ", tags = "user", security = @SecurityRequirement(name = "token_auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update success"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/users/update", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> updateUser(@RequestBody @Valid NoNullRequiredUserVM noNullRequiredUserVM);
    
    @Operation(
            summary = "Export users to excel",
            description = "Export users to excel",
            tags = "user",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<?> exportUsersToExcel(HttpServletResponse response);

    @Operation(
            summary = "Download Excel Template For Importing Users",
            description = "Download Excel Template For Importing Users",
            tags = "user",
            security = @SecurityRequirement(name = "token_auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/template/excel", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<Resource> downloadUserExcelTemplate(HttpServletResponse response);
}
