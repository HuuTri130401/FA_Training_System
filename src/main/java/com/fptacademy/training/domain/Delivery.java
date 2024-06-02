package com.fptacademy.training.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Table(name = "deliveries")
@Entity
public class Delivery implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // @Schema(example = "1")
  private Long id;

  @Column(length = 100)
  private String name;

  private String code;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Transient
  private Double present;
}
