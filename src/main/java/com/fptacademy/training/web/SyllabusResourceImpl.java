package com.fptacademy.training.web;

import com.fptacademy.training.domain.Assessment;
import com.fptacademy.training.domain.Lesson;
import com.fptacademy.training.domain.Material;
import com.fptacademy.training.domain.Session;
import com.fptacademy.training.domain.Syllabus;
import com.fptacademy.training.domain.TrainingPrinciple;
import com.fptacademy.training.domain.Unit;
import com.fptacademy.training.domain.enumeration.SyllabusStatus;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.SyllabusRepository;
import com.fptacademy.training.service.SyllabusService;
import com.fptacademy.training.service.dto.SyllabusDto;
import com.fptacademy.training.service.dto.SyllabusDto.SyllabusDetailDto;
import com.fptacademy.training.service.dto.SyllabusDto.SyllabusListDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SyllabusResourceImpl {

  private final SyllabusRepository syllabusRepository;
  private final SyllabusService syllabusService;

  @Operation(
    summary = "List all syllabuses",
    description = "sort createby : createdBy.code,desc || createdBy.code,asc",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @GetMapping(value = "/syllabuses")
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<Page<SyllabusListDto>> getAllSyllabuses(
    @org.springdoc.api.annotations.ParameterObject Pageable pageable,
    @RequestParam(required = false) String[] keywords,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant[] createDate,
    Authentication authentication,
    @Schema(allowableValues = { "false", "true" }) @RequestParam(required = true) Boolean draft
  ) {
    return ResponseEntity.ok(
      syllabusService.findAll(SyllabusRepository.searchByKeywordsOrBycreateDates(keywords, createDate, authentication, draft), pageable)
    );
  }

  @Operation(
    summary = "Show syllabus by id",
    description = "Syllabus Input: id(long)",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  @GetMapping("/syllabuses/{id}")
  public ResponseEntity<SyllabusDetailDto> getSyllabus(@PathVariable Long id) {
    return syllabusService
      .findOne(id)
      .map(response -> ResponseEntity.ok().body(response))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    summary = "Delete syllabus by id",
    description = "Syllabus Input: id(long)",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @DeleteMapping("/syllabuses/{id}")
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<?> deleteSyllabus(@PathVariable Long id) {
    Syllabus syl = syllabusRepository
      .findById(id)
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    syl.setStatus(SyllabusStatus.REJECTED);
    syllabusService.delete(syl);
    return ResponseEntity.ok("OK");
  }

  @Operation(
    summary = "De-active syllabus by id",
    description = "Syllabus Input: id(long)",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PutMapping("/syllabuses/de-active/{id}")
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<?> deActiveSyllabus(@PathVariable Long id) {
    Syllabus syllabus = syllabusRepository
      .findById(id)
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (syllabus.getStatus().equals(SyllabusStatus.DEACTIVATED)) {
      throw new ResourceBadRequestException("syllabus is already DEACTIVATED");
    }
    syllabus.setStatus(SyllabusStatus.DEACTIVATED);
    syllabusService.delete(syllabus);
    return ResponseEntity.ok("OK");
  }

  @Operation(
    summary = "Active syllabus by id",
    description = "Syllabus Input: id(long)",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PutMapping("/syllabuses/active/{id}")
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<?> activeSyllabus(@PathVariable Long id) {
    Syllabus syllabus = syllabusRepository
      .findById(id)
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (syllabus.getStatus().equals(SyllabusStatus.ACTIVATED)) {
      throw new ResourceBadRequestException("syllabus is already ACTIVATED");
    }
    syllabus.setStatus(SyllabusStatus.ACTIVATED);
    syllabusService.delete(syllabus);
    return ResponseEntity.ok("OK");
  }

  @Operation(
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
        @Content(
          mediaType = "application/json",
          schema = @Schema(
            allOf = SyllabusDetailDto.class,
            description = "post Syllabus",
            externalDocs = @ExternalDocumentation(url = "https://github.com/"),
            title = "Syllabus",
            defaultValue = "{" +
            "    \"name\": \"string\"," +
            "    \"attendeeNumber\": 0," +
            "    \"status\": \"ACTIVATED\"," +
            "    \"technicalRequirement\": \"string\"," +
            "    \"courseObjective\": \"string\"," +
            "    \"trainingPrinciple\": {" +
            "        \"training\": \"string\"," +
            "        \"reTest\": \"string\"," +
            "        \"marking\": \"string\"," +
            "        \"waiverCriteria\": \"string\"," +
            "        \"others\": \"string\"" +
            "    }," +
            "    \"level\": {" +
            "        \"id\": 1" +
            "    }," +
            "    \"assessment\": {" +
            "        \"quiz\": 0," +
            "        \"assignment\": 0," +
            "        \"finalField\": 0," +
            "        \"finalPractice\": 0," +
            "        \"finalTheory\": 0," +
            "        \"gpa\": 0" +
            "    }," +
            "    \"sessions\": [" +
            "        {" +
            "            \"index\": 0," +
            "            \"name\": \"string\"," +
            "            \"units\": [" +
            "                {" +
            "                    \"title\": \"string\"," +
            "                    \"name\": \"string\"," +
            "                    \"index\": 0," +
            "                    \"lessons\": [" +
            "                        {" +
            "                            \"name\": \"string\"," +
            "                            \"duration\": 0," +
            "                            \"index\": 0," +
            "                            \"outputStandard\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"formatType\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"delivery\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"materials\": [" +
            "                                {" +
            "                                    \"name\": \"string\"," +
            "                                    \"fileUrl\": \"string\"" +
            "                                }" +
            "                            ]" +
            "                        }" +
            "                    ]" +
            "                }" +
            "            ]" +
            "        }" +
            "    ]" +
            "}"
          )
        ),
        @Content(
          mediaType = "application/schema+json",
          schema = @Schema(implementation = SyllabusDetailDto.class),
          examples = {
            @ExampleObject(name = "default", description = "default json", summary = "default json"),
            @ExampleObject(name = "name 1", description = "not summary , have name", value = "value 1"),
            @ExampleObject(name = "name 2", description = "have summary , not name", summary = "summary 2", value = "value 2"),
          }
        ),
        @Content(mediaType = "application/vnd.api+json", schema = @Schema(implementation = SyllabusDetailDto.class)),
        @Content(mediaType = "application/vnd.geo+json", schema = @Schema(implementation = SyllabusDetailDto.class)),
      }
    ),
    summary = "Add new syllabus",
    description = "Syllabus Input: name(string), attendeeNumber, status, duration" +
    "technicalRequirement, courseObjective, trainingPrinciple, level, session" +
    "unit, lesson, formatType, delivery, materials, assessment",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Not found", responseCode = "404", content = @Content) }
  )
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  @PostMapping("/syllabuses")
  public ResponseEntity<SyllabusDetailDto> createSyllabus(@RequestBody @Valid SyllabusDetailDto syllabus, Errors errors) {
    if (errors.hasErrors()) {
      throw new ResourceBadRequestException(errors.getFieldError().getDefaultMessage());
    }
    return ResponseEntity.ok(syllabusService.save(syllabus));
  }

  @Operation(
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
        @Content(
          mediaType = "application/json",
          schema = @Schema(
            allOf = SyllabusDetailDto.class,
            description = "put Syllabus",
            externalDocs = @ExternalDocumentation(url = "https://github.com/"),
            title = "Syllabus",
            defaultValue = "{" +
            "    \"id\": 0," +
            "    \"name\": \"string\"," +
            "    \"attendeeNumber\": 0," +
            "    \"status\": \"ACTIVATED\"," +
            "    \"technicalRequirement\": \"string\"," +
            "    \"courseObjective\": \"string\"," +
            "    \"trainingPrinciple\": {" +
            "        \"id\": 0," +
            "        \"training\": \"string\"," +
            "        \"reTest\": \"string\"," +
            "        \"marking\": \"string\"," +
            "        \"waiverCriteria\": \"string\"," +
            "        \"others\": \"string\"" +
            "    }," +
            "    \"level\": {" +
            "        \"id\": 1" +
            "    }," +
            "    \"assessment\": {" +
            "        \"id\": 0," +
            "        \"quiz\": 0," +
            "        \"assignment\": 0," +
            "        \"finalField\": 0," +
            "        \"finalPractice\": 0," +
            "        \"finalTheory\": 0," +
            "        \"gpa\": 0" +
            "    }," +
            "    \"sessions\": [" +
            "        {" +
            "            \"id\": 0," +
            "            \"index\": 0," +
            "            \"name\": \"string\"," +
            "            \"units\": [" +
            "                {" +
            "                    \"id\": 0," +
            "                    \"title\": \"string\"," +
            "                    \"name\": \"string\"," +
            "                    \"index\": 0," +
            "                    \"lessons\": [" +
            "                        {" +
            "                            \"id\": 0," +
            "                            \"name\": \"string\"," +
            "                            \"duration\": 0," +
            "                            \"index\": 0," +
            "                            \"outputStandard\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"formatType\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"delivery\": {" +
            "                                \"id\": 1" +
            "                            }," +
            "                            \"materials\": [" +
            "                                {" +
            "                                    \"id\": 0," +
            "                                    \"name\": \"string\"," +
            "                                    \"fileUrl\": \"string\"" +
            "                                }" +
            "                            ]" +
            "                        }" +
            "                    ]" +
            "                }" +
            "            ]" +
            "        }" +
            "    ]" +
            "}"
          )
        ),
        @Content(
          mediaType = "application/schema+json",
          schema = @Schema(implementation = SyllabusDetailDto.class),
          examples = {
            @ExampleObject(name = "default", description = "default json", summary = "default json"),
            @ExampleObject(name = "name 1", description = "not summary , have name", value = "value 1"),
            @ExampleObject(name = "name 2", description = "have summary , not name", summary = "summary 2", value = "value 2"),
          }
        ),
        @Content(mediaType = "application/vnd.api+json", schema = @Schema(implementation = SyllabusDetailDto.class)),
        @Content(mediaType = "application/vnd.geo+json", schema = @Schema(implementation = SyllabusDetailDto.class)),
      }
    ),
    summary = "Update syllabus",
    description = "Syllabus Input: name(string), attendeeNumber, status, duration" +
    "technicalRequirement, courseObjective, trainingPrinciple, level, session" +
    "unit, lesson, formatType, delivery, materials, assessment",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PutMapping("/syllabuses")
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<SyllabusDetailDto> updateSyllabus(@RequestBody @Valid SyllabusDetailDto syllabus, Errors errors) {
    syllabusRepository
      .findById(syllabus.getId())
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED))
      .orElseThrow(() -> new ResourceBadRequestException("syllabus id not found!"));
    if (errors.hasErrors()) {
      throw new ResourceBadRequestException(errors.getFieldError().getDefaultMessage());
    }
    return syllabusService
      .update(syllabus)
      .map(response -> ResponseEntity.ok().body(response))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    summary = "Duplicate syllabus",
    description = "Syllabus Input: id(long)",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PostMapping("/syllabuses/duplicate/{id}")
  @Transactional
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  public ResponseEntity<Syllabus> duplicateSyllabus(@PathVariable Long id) {
    ModelMapper map = new ModelMapper();
    Syllabus syllabus = syllabusRepository
      .findById(id)
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED))
      .orElseThrow(() -> new ResourceBadRequestException("syllabus id not found!"));
    map
      .createTypeMap(Syllabus.class, Syllabus.class)
      .addMappings(mapper -> {
        mapper.skip(Syllabus::setId);
        mapper
          .using((Converter<String, String>) ctx -> ctx.getSource().contains("duplicate") ? ctx.getSource() : ctx.getSource() + " (duplicate)")
          .map(Syllabus::getName, Syllabus::setName);
        mapper.map(src -> Long.toString(UUID.randomUUID().getMostSignificantBits() & 0xffffff, 36).toUpperCase(), Syllabus::setCode);
      });
    map.createTypeMap(TrainingPrinciple.class, TrainingPrinciple.class).addMappings(mapper -> mapper.skip(TrainingPrinciple::setId));
    map.createTypeMap(Assessment.class, Assessment.class).addMappings(mapper -> mapper.skip(Assessment::setId));
    map.createTypeMap(Session.class, Session.class).addMappings(mapper -> mapper.skip(Session::setId));
    map.createTypeMap(Unit.class, Unit.class).addMappings(mapper -> mapper.skip(Unit::setId));
    map.createTypeMap(Lesson.class, Lesson.class).addMappings(mapper -> mapper.skip(Lesson::setId));
    map.createTypeMap(Material.class, Material.class).addMappings(mapper -> mapper.skip(Material::setId));
    return ResponseEntity.ok(syllabusRepository.save(map.map(syllabus, Syllabus.class)));
  }

  @Operation(
    summary = "Get list of syllabuses by name",
    description = "Get list of syllabuses by name",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth")
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Found syllabuses"),
      @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid JWT", content = @Content),
      @ApiResponse(responseCode = "403", description = "Access denied, do not have permission to access this resource", content = @Content),
    }
  )
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/syllabuses/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SyllabusDto.SyllabusListDto>> getSyllabusesByName(String name) {
    List<SyllabusDto.SyllabusListDto> syllabusDTOs = syllabusService.findActivatedSyllabusesByName(name);
    return ResponseEntity.status(HttpStatus.OK).body(syllabusDTOs);
  }

  @Operation(
    summary = "Get template syllabus",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  @GetMapping(value = "/syllabuses/template", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getTemplateSyllabus() {
    Resource resource = new ClassPathResource("templates/Syllabus-template.xlsx");
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
    headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel");
    return ResponseEntity.ok().headers(headers).body(resource);
  }

  @Operation(
    summary = "Import syllabus",
    description = "Syllabus Input: file, scanning, handle",
    tags = "Syllabus",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @PreAuthorize("!hasAuthority('Syllabus_AccessDenied')")
  @PostMapping(value = "/syllabuses/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> importSyllabus(
    @RequestPart(value = "file", required = true) MultipartFile file,
    @Schema(description = "", type = "array") @RequestParam(required = false) String[] scanning,
    @Schema(allowableValues = { "allow", "replace", "skip" }) @RequestParam(required = true) String handle
  ) {
    return ResponseEntity.ok(syllabusService.importExcel(file, scanning, handle));
  }
}
