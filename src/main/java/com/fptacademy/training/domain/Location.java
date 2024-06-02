package com.fptacademy.training.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Table(name = "locations")
@Entity
public class Location implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String city;
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String fsu;

    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String code;
}
