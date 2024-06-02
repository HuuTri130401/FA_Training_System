package com.fptacademy.training.web;

import com.fptacademy.training.domain.OutputStandard;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.OutputStandardRepository;
import com.fptacademy.training.service.OutputStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OutputStandardResourceImpl {

  private final OutputStandardService outputStandardService;
  private final OutputStandardRepository outputStandardRepository;

  @Operation(
    summary = "Add new output standard",
    description = "OutputStandard Input: name(string)",
    tags = "Output standard",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(
        description = "The length of name must be in [1 - 10]",
        responseCode = "400",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(name = "Example", value = "{\"name\": \"H₂SO₄HNO₃H₃PO₄H₂O\"}")
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
      @ApiResponse(description = "This name is already exists", responseCode = "500", content = @Content),
    }
  )
  @PostMapping("/outputStandards")
  public ResponseEntity<OutputStandard> createOutputStandard(@RequestBody OutputStandard OutputStandardDTO) {
    if (OutputStandardDTO.getId() != null) {
      throw new ResourceBadRequestException("A new OutputStandard cannot already have an ID");
    }
    OutputStandard result = outputStandardService.save(OutputStandardDTO);
    return ResponseEntity.ok(result);
  }

  @Operation(
    summary = "Update output standard by id",
    description = "OutputStandard Input: id(long), name(string)",
    tags = "Output standard",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(
        description = "The length of name must be in [1 - 10]",
        responseCode = "400",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄HNO₃H₃PO₄H₂O\"}")
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
      @ApiResponse(description = "This name is already exists", responseCode = "500", content = @Content),
    }
  )
  @PutMapping(value = "/outputStandards/{id}")
  public ResponseEntity<OutputStandard> updateOutputStandard(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody OutputStandard OutputStandardDTO
  ) {
    if (OutputStandardDTO.getId() == null) {
      throw new ResourceBadRequestException("id null");
    }
    if (!Objects.equals(id, OutputStandardDTO.getId())) {
      throw new ResourceBadRequestException("id invalid");
    }

    if (!outputStandardRepository.existsById(id)) {
      throw new ResourceBadRequestException("Entity not found id ");
    }

    Optional<OutputStandard> result = outputStandardService.update(OutputStandardDTO);

    return result.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    summary = "List all output standards",
    description = "OutputStandard Input: null",
    tags = "Output standard",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(
            name = "Example",
            value = "[{\"id\": 1, \"name\": \"H₂SO₄\"}" +
            ", {\"id\": 2, \"name\": \"HNO₃\"}" +
            ", {\"id\": 3, \"name\": \"H₃PO₄\"}" +
            ", {\"id\": 4, \"name\": \"H₂O\"}]"
          )
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
    }
  )
  @GetMapping("/outputStandards")
  public ResponseEntity<List<OutputStandard>> getAllOutputStandards() {
    List<OutputStandard> list = outputStandardService.findAll();
    return ResponseEntity.ok().body(list);
  }

  @Operation(
    summary = "Get an output standard by id",
    description = "OutputStandard Input: id(long)",
    tags = "Output standard",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = OutputStandard.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
    }
  )
  @GetMapping("/outputStandards/{id}")
  public ResponseEntity<OutputStandard> getOutputStandard(@PathVariable Long id) {
    Optional<OutputStandard> outputStandard = outputStandardService.findOne(id);
    return outputStandard.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    deprecated = true,
    summary = "Delete an output standard by id",
    description = "OutputStandard Input: id(long)",
    tags = "Output standard",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(description = "Success | OK", responseCode = "200", content = @Content),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
    }
  )
  @DeleteMapping("/outputStandards/{id}")
  public ResponseEntity<?> deleteOutputStandard(@PathVariable Long id) {
    outputStandardService.delete(id);
    return ResponseEntity.ok("OK");
  }
}
