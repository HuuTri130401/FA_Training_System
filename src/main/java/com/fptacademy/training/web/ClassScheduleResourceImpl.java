package com.fptacademy.training.web;

import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.service.ClassScheduleService;
import com.fptacademy.training.service.dto.ReturnClassScheduleDto;
import com.fptacademy.training.service.mapper.ClassScheduleMapper;
import com.fptacademy.training.web.api.ClassScheduleResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClassScheduleResourceImpl implements ClassScheduleResource {

    private final ClassScheduleService classScheduleService;
    private final ClassScheduleMapper classScheduleMapper;

    @Override
    public ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleByDate(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {
        List<ClassSchedule> classSchedules = classScheduleService.getFilterClassScheduleByDate(date, className, classCode, city);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(classSchedules);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleByWeek(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {

        List<ClassSchedule> classSchedules = classScheduleService.getFilterClassScheduleInAWeek(date, className, classCode, city);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(classSchedules);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleOfCurrentUser(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {

        List<ClassSchedule> classSchedules = classScheduleService.getFilterClassScheduleOfCurrentUserByDate(date, className, classCode, city);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(classSchedules);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReturnClassScheduleDto>> getAllClassScheduleOfCurrentUserByWeek(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {

        List<ClassSchedule> classSchedules = classScheduleService.getFilterClassScheduleOfCurrentUserInAWeek(date, className, classCode, city);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(classSchedules);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
