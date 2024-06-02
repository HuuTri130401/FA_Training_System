package com.fptacademy.training.domain;

import com.fptacademy.training.service.util.ListToStringConverter;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "roles")
@Entity
public class Role implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    @Convert(converter = ListToStringConverter.class)
    private List<String> permissions;

    public Role(String name) {
        this.name = name;
    }
}
