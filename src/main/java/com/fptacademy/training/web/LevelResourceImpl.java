package com.fptacademy.training.web;

import com.fptacademy.training.domain.Level;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.LevelRepository;
import com.fptacademy.training.service.LevelService;
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
public class LevelResourceImpl {

  private final LevelService levelService;
  private final LevelRepository levelRepository;

  @Operation(
    summary = "Add new level",
    description = "Level Input: name(string)",
    tags = "Level",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Level.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(description = "The length of name must be in [1 - 50]", responseCode = "400", content = @Content),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
      @ApiResponse(description = "This name is already exists", responseCode = "500", content = @Content),
    }
  )
  @PostMapping("/levels")
  public ResponseEntity<Level> createLevel(@RequestBody Level level) {
    if (level.getId() != null) {
      throw new ResourceBadRequestException("A new Level cannot already have an ID");
    }
    Level result = levelService.save(level);
    return ResponseEntity.ok(result);
  }

  @Operation(
    summary = "Update a level by id",
    description = "Level Input: id(long), name(string)",
    tags = "Level",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Level.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(description = "The length of name must be in [1 - 50]", responseCode = "400", content = @Content),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
      @ApiResponse(description = "This name is already exists", responseCode = "500", content = @Content),
    }
  )
  @PutMapping(value = "/levels/{id}")
  public ResponseEntity<Level> updateLevel(@PathVariable(value = "id", required = false) final Long id, @RequestBody Level level) {
    if (level.getId() == null) {
      throw new ResourceBadRequestException("id null");
    }
    if (!Objects.equals(id, level.getId())) {
      throw new ResourceBadRequestException("id invalid");
    }

    if (!levelRepository.existsById(id)) {
      throw new ResourceBadRequestException("Entity not found id ");
    }

    Optional<Level> result = levelService.update(level);

    return result.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    summary = "List all levels",
    description = "Level Input: null",
    tags = "Level",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Level.class),
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
  @GetMapping("/levels")
  public ResponseEntity<List<Level>> getAllLevels() {
    List<Level> list = levelService.findAll();
    return ResponseEntity.ok().body(list);
  }

  @Operation(
    summary = "Show a level by id",
    description = "Level Input: id(long)",
    tags = "Level",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Level.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"H₂SO₄\"}")
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
    }
  )
  @GetMapping("/levels/{id}")
  public ResponseEntity<Level> getLevel(@PathVariable Long id) {
    Optional<Level> level = levelService.findOne(id);
    return level.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    deprecated = true,
    summary = "Delete level by id",
    description = "Level Input: id(long)",
    tags = "Level",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(description = "Success | OK", responseCode = "200", content = @Content),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
    }
  )
  @DeleteMapping("/levels/{id}")
  public ResponseEntity<?> deleteLevel(@PathVariable Long id) {
    levelService.delete(id);
    return ResponseEntity.ok("OK");
  }
}
