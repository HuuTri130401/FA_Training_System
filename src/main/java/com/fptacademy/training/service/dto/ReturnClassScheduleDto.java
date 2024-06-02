package com.fptacademy.training.service.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReturnClassScheduleDto {

    private Long classId;
    private String classCode;
    private String className;
    private int duration;
    private int currentClassDay;
    private String city;
    private String fsu;
    private String type;
    private LocalDate date;
    private LocalTime startAt;
    private LocalTime finishAt;
    private List<ReturnUnitDto> units;
    private List<ReturnUserDto> trainers;
    private List<ReturnUserDto> classAdmins;

}
