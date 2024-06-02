package com.fptacademy.training.domain;

import com.fptacademy.training.domain.enumeration.SyllabusStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
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
@Table(name = "syllabuses")
@Entity
public class Syllabus extends AbstractAuditEntity implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100)
  private String name;

  @Column(length = 20)
  private String code;

  private Integer attendeeNumber;

  @Column(length = 20)
  @Enumerated(EnumType.STRING)
  private SyllabusStatus status;

  private Integer duration;

  private Float version;

  @Column(columnDefinition = "TEXT")
  private String technicalRequirement;

  @Column(columnDefinition = "TEXT")
  private String courseObjective;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "training_principle_id")
  private TrainingPrinciple trainingPrinciple;

  @ManyToOne
  @JoinColumn(name = "level_id")
  private Level level;

  @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Session> sessions = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "assessment_id")
  private Assessment assessment;

  @PrePersist
  public void prePersist() {
    this.sessions.forEach(s -> s.setSyllabus(this));
  }
}
