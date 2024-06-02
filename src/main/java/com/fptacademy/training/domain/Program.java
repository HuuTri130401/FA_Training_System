package com.fptacademy.training.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Table(name = "programs")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "graph.Program.syllabus.session", includeAllAttributes = true,
                attributeNodes = @NamedAttributeNode(value = "syllabuses", subgraph = "subgraph.syllabus"),
                subgraphs = @NamedSubgraph(name = "subgraph.syllabus", attributeNodes = @NamedAttributeNode(value = "sessions"))
        )
})
public class Program extends AbstractAuditEntity implements Serializable {
    private static final Long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String name;
    private Boolean activated = false;
    @ManyToMany
    @JoinTable(
            name = "program_syllabus",
            joinColumns = { @JoinColumn(name = "program_id") },
            inverseJoinColumns = { @JoinColumn(name = "syllabus_id") }
    )
    @OrderColumn(name = "syllabus_index")
    private List<Syllabus> syllabuses;
}
