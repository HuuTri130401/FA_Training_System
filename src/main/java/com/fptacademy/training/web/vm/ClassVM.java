package com.fptacademy.training.web.vm;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ClassVM(
        String name,
        Long programId,
        LocalTime startAt,
        LocalTime finishAt,
        String detailLocation,
        Long fsuId,
        String contactPoint,
        Long attendeeId,
        int planned,
        int accepted,
        int actual,
        @Schema(description = "Can omit this filed")
        String others,
        List<Long> userIds,
        List<LocalDate> studyDates,
        @Schema(description = "When save as draft, send this field with 'DRAFT', otherwise, send with 'PLANNING' or " +
                "'OPENNING'")
        String status //DRAFT or PLANNING or OPENNING
) {
}
