package com.fptacademy.training.web;

import com.fptacademy.training.domain.FormatType;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.FormatTypeRepository;
import com.fptacademy.training.service.FormatTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Objects;
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
public class FormatTypeResourceImpl {

  private final FormatTypeService formatTypeService;
  private final FormatTypeRepository formatTypeRepository;

  @Operation(tags = "Format type", security = @SecurityRequirement(name = "token_auth"))
  @PostMapping(path = "/formattype")
  public ResponseEntity<FormatType> createFormatType(@RequestBody FormatType formatType) {
    if (formatType.getId() != null) {
      throw new ResourceBadRequestException("A new formatType cannot already have an ID");
    }
    return ResponseEntity.ok().body(formatTypeService.save(formatType));
  }

  @Operation(tags = "Format type", security = @SecurityRequirement(name = "token_auth"))
  @PutMapping(path = "/formattype/{id}")
  public ResponseEntity<FormatType> updateFormatType(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody FormatType formatType
  ) {
    if (formatType.getId() == null) {
      throw new ResourceBadRequestException("id null");
    }
    if (!Objects.equals(id, formatType.getId())) {
      throw new ResourceBadRequestException("id invalid");
    }

    if (!formatTypeRepository.existsById(id)) {
      throw new ResourceBadRequestException("Entity not found id ");
    }

    return formatTypeService
      .update(formatType)
      .map(response -> ResponseEntity.ok().body(response))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(tags = "Format type", security = @SecurityRequirement(name = "token_auth"))
  @GetMapping("/formattype")
  public ResponseEntity<List<FormatType>> getFormatType() {
    return ResponseEntity.ok().body(formatTypeService.getAll());
  }

  @Operation(tags = "Format type", security = @SecurityRequirement(name = "token_auth"))
  @GetMapping("/formattype/{id}")
  public ResponseEntity<FormatType> getFormatType(@PathVariable Long id) {
    return formatTypeService
      .getOne(id)
      .map(response -> ResponseEntity.ok().body(response))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(deprecated = true, tags = "Format type", security = @SecurityRequirement(name = "token_auth"))
  @DeleteMapping("/formattype/{id}")
  public ResponseEntity<?> deleteFormatType(@PathVariable Long id) {
    formatTypeService.delete(id);
    return ResponseEntity.ok("OK");
  }
}
