package com.fptacademy.training.web;

import com.fptacademy.training.domain.Assessment;
import com.fptacademy.training.domain.Delivery;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.DeliveryRepository;
import com.fptacademy.training.service.DeliveryService;
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
public class DeliveryResourceImpl {

  private final DeliveryService deliveryService;
  private final DeliveryRepository deliveryRepository;

  @Operation(
    summary = "Add new delivery",
    description = "Delivery Input: name(string)",
    tags = "Delivery",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Assessment.class),
          examples = @ExampleObject(
            name = "Example",
            value = "[{\"id\": 1, \"name\": \"Assignment/Lab\"}" +
            ", {\"id\": 2, \"name\": \"Concept/Lecture\"}" +
            ", {\"id\": 3, \"name\": \"Guide/Review\"}" +
            ", {\"id\": 4, \"name\": \"Test/Quiz\"}" +
            ", {\"id\": 5, \"name\": \"Exam\"}" +
            ", {\"id\": 6, \"name\": \"Seminar/Workshop\"}]"
          )
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
      @ApiResponse(description = "The number is out of the range", responseCode = "400", content = @Content),
    }
  )
  @PostMapping("/deliveries")
  public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
    if (delivery.getId() != null) {
      throw new ResourceBadRequestException("A new Delivery cannot already have an ID");
    }
    Delivery result = deliveryService.save(delivery);
    return ResponseEntity.ok(result);
  }

  @Operation(
    summary = "Update a delivery by id",
    description = "Delivery Input: id(long), name(string)",
    tags = "Delivery",
    security = @SecurityRequirement(name = "token_auth"),
    responses = {
      @ApiResponse(
        description = "Success | OK",
        responseCode = "200",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Assessment.class),
          examples = @ExampleObject(name = "Example", value = "{\"id\": 1, \"name\": \"ABCD/TEST\"}")
        )
      ),
      @ApiResponse(description = "Not found", responseCode = "404"),
      @ApiResponse(description = "The number is out of the range", responseCode = "400"),
    }
  )
  @PutMapping(value = "/deliveries/{id}")
  public ResponseEntity<Delivery> updateDelivery(@PathVariable(value = "id", required = false) final Long id, @RequestBody Delivery delivery) {
    if (delivery.getId() == null) {
      throw new ResourceBadRequestException("id null");
    }
    if (!Objects.equals(id, delivery.getId())) {
      throw new ResourceBadRequestException("id invalid");
    }

    if (!deliveryRepository.existsById(id)) {
      throw new ResourceBadRequestException("Entity not found id ");
    }

    Optional<Delivery> result = deliveryService.update(delivery);

    return result.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    summary = "List all deliveries",
    description = "Delivery Input: null",
    tags = "Delivery",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @GetMapping("/deliveries")
  public ResponseEntity<List<Delivery>> getAllDeliveries() {
    List<Delivery> list = deliveryService.findAll();
    return ResponseEntity.ok().body(list);
  }

  @Operation(
    summary = "Show a delivery by id",
    description = "Delivery Input: id(long)",
    tags = "Delivery",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @GetMapping("/deliveries/{id}")
  public ResponseEntity<Delivery> getDelivery(@PathVariable Long id) {
    Optional<Delivery> delivery = deliveryService.findOne(id);
    return delivery.map(response -> ResponseEntity.ok().body(response)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Operation(
    deprecated = true,
    summary = "Delete a delivery by id",
    description = "Delivery Input: id(long)",
    tags = "Delivery",
    security = @SecurityRequirement(name = "token_auth"),
    responses = { @ApiResponse(description = "Success | OK", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404") }
  )
  @DeleteMapping("/deliveries/{id}")
  public ResponseEntity<?> deleteDelivery(@PathVariable Long id) {
    deliveryService.delete(id);
    return ResponseEntity.ok("OK");
  }
}
