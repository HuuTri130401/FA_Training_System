package com.fptacademy.training.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "assessments")
@Entity
public class Assessment implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(precision = 3, scale = 2)
  private Float quiz;

  @Column(precision = 3, scale = 2)
  private Float assignment;

  @Column(name = "final", precision = 3, scale = 2)
  private Float finalField;

  @Column(precision = 3, scale = 2)
  private Float finalPractice;

  @Column(precision = 3, scale = 2)
  private Float finalTheory;

  @Column(precision = 3, scale = 2)
  private Float gpa;
}
