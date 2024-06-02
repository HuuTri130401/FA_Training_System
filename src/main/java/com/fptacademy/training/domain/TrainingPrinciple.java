package com.fptacademy.training.domain;

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
@Table(name = "training_principle")
@Entity
public class TrainingPrinciple {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String training;

  @Column(columnDefinition = "TEXT")
  private String reTest;

  @Column(columnDefinition = "TEXT")
  private String marking;

  @Column(columnDefinition = "TEXT")
  private String waiverCriteria;

  @Column(columnDefinition = "TEXT")
  private String others;
}
