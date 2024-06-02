package com.fptacademy.training.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file_storage")
@Entity
public class FileStorage implements Serializable {

    private static final Long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String url;

    private Instant date;

    private String description;

    public FileStorage(String url, String description) {
        this.url = url ;
        this.date = Instant.now();
        this.description = description;
    }
}
