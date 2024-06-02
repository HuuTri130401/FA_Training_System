package com.fptacademy.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnPageDto<T> {
    private Integer totalPages;

    private Long totalElements;

    private Integer size;

    private Integer page;

    private T contents;
}
