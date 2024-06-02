package com.fptacademy.training.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "units")
@Entity
public class Unit implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100)
  private String title;

  @Column(length = 100)
  private String name;

  @Column(name = "`index`")
  private Integer index;

  private Double totalDurationLesson;

  @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Lesson> lessons = new ArrayList<>();

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "session_id")
  private Session session;

  @PrePersist
  public void prePersist() {
    this.lessons.forEach(l -> l.setUnit(this));
  }
}
