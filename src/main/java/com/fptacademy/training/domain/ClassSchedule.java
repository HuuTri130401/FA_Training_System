package com.fptacademy.training.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "class_schedules")
@Entity
public class ClassSchedule implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "class_detail_id")
    private ClassDetail classDetail;
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private User trainer;
    private LocalDate studyDate;
    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
}
