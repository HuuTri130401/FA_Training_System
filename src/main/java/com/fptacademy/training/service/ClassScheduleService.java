package com.fptacademy.training.service;

import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.ClassScheduleRepository;
import com.fptacademy.training.service.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    private final UserService userService;

    public List<ClassSchedule> getFilterClassScheduleByDate(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {
        if (date == null) {
            throw new ResourceBadRequestException("Bad request: date is null");
        }
        return classScheduleRepository.findFilterActiveClassByStudyDate(
                date,
                ClassStatus.OPENNING.toString(),
                null,
                className,
                classCode,
                city
        );
    }

    public List<ClassSchedule> getFilterClassScheduleOfCurrentUserByDate(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {
        if (date == null) {
            throw new ResourceBadRequestException("Bad request: date is null");
        }
        User user = userService.getCurrentUserLogin();
        return classScheduleRepository.findFilterActiveClassByStudyDate(
                date,
                ClassStatus.OPENNING.toString(),
                user.getId(),
                className,
                classCode,
                city
        );
    }

    //testing here
    public List<ClassSchedule> getFilterClassScheduleOfCurrentUserInAWeek(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {
        if (date == null) {
            throw new ResourceBadRequestException("Bad request: date is null");
        }
        LocalDate firstDate = DateTimeUtil.getFirstDateOfCurrentWeek(date);
        LocalDate lastDate = DateTimeUtil.getLastDateOfCurrentWeek(date);
        User user = userService.getCurrentUserLogin();

        return classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                firstDate,
                lastDate,
                ClassStatus.OPENNING.toString(),
                user.getId(),
                className,
                classCode,
                city
        );
    }

    public List<ClassSchedule> getFilterClassScheduleInAWeek(
            LocalDate date,
            List<String> className,
            List<String> classCode,
            List<String> city
    ) {
        if (date == null) {
            throw new ResourceBadRequestException("Bad request: date is null.");
        }
        LocalDate firstDate = DateTimeUtil.getFirstDateOfCurrentWeek(date);
        LocalDate lastDate = DateTimeUtil.getLastDateOfCurrentWeek(date);

        return classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                firstDate,
                lastDate,
                ClassStatus.OPENNING.toString(),
                null,
                className,
                classCode,
                city
        );
    }

    public int getCurrentClassDay(Long classId, Long classScheduleId) {
        if (classId == null || classScheduleId == null) {
            throw new ResourceBadRequestException("Bad request for classId and classScheduleId");
        }
        Integer day = classScheduleRepository.getCurrentClassDayOfClassSchedule(classId, classScheduleId);
        if (day == null) {
            return -1;
        }
        return day;
    }

}
