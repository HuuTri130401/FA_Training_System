package com.fptacademy.training.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassDetailDto implements Serializable{
    private static final Long serialVersionUID = 1L;
    private Long class_id;
    private Integer accepted;
    private Integer actual;
    private LocalTime finish_at;
    private String others;
    private Integer planned;
    private LocalTime start_at;
    private String status;
    private String detailLocation;
    private String contactPoint;
    private ClassSimplified classInfo;
    private AttendeeSimplified attendee;
    private LocationSimplified location;
    private List<UserSimplified> trainer;
    private List<UserSimplified> admin;
    private ProgramSimplified program;
    private List<ScheduleSimplified> schedules;

    @Getter
    @AllArgsConstructor
    public static class ClassSimplified {
        private Long id;
        private String name;
        private String code;
        private Integer duration;
        private UserSimplified created_by;
        private Instant createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class ProgramSimplified {
        private Long id;
        private String name;
        private Integer durationInDays;
        private Float durationInHours;
    }

    @Getter
    @AllArgsConstructor
    public static class AttendeeSimplified {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class LocationSimplified {
        private Long id;
        private String city;
        private String fsu;
    }

    @Getter
    @AllArgsConstructor
    public static class UserSimplified {
        private Long id;
        private String name;
        private String Email;
        private String code;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleSimplified{
        private LocalDate study_date;
        private Long syllabusId;
        private String syllabusName;
        private List<UnitSimplified> units;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitSimplified {
        private Long id;
        private int index;
        private String name;
    }

}
