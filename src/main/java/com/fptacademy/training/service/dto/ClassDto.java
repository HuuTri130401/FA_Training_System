package com.fptacademy.training.service.dto;
import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto implements Serializable{
    private static final Long serialVersionUID = 1L;
    private Long id;
    private Instant created_at;
    private String code;
    private Integer duration;
    private String name;
    private String status;
    private Creator created_by;
    private AttendeeSimplified  attendee;
    private LocationSimplified location_id;

    @Getter
    @AllArgsConstructor
    public static class Creator {
        private Long id;
        private String name;
        private String code;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AttendeeSimplified {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class LocationSimplified {
        private Long id;
        private String city;
        private String fsu;
    }
}
