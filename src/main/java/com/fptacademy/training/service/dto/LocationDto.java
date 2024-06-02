package com.fptacademy.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto implements Serializable {

    private Long id;
    private String city;
    private String code;
    private String fsu;
}
