package com.fptacademy.training.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
@ToString
@Table(name = "classes")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Class extends AbstractAuditEntity implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String name;
    @Size(max = 50)
    @Column(length = 50, nullable = false, unique = true)
    private String code;
    private Integer duration;
    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;
    @OneToOne(mappedBy = "classField", cascade = CascadeType.ALL)
    private ClassDetail classDetail;
}
