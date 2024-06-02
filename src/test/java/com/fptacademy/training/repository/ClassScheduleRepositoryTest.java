package com.fptacademy.training.repository;

import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ClassScheduleRepositoryTest {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Test
    void findFilterActiveClassByStudyDateShouldWork() {
        LocalDate date = LocalDate.now();

        List<ClassSchedule> classSchedules = classScheduleRepository.findFilterActiveClassByStudyDate(
                date,
                ClassStatus.OPENNING.toString(),
                null,
                Arrays.asList("abc", "xyz", "123"),
                Arrays.asList("className1", "className2"),
                Arrays.asList("Ho Chi Minh", "Ha Noi")
        );

        assertNotNull(classSchedules);
    }

    @Test
    void findFilterActiveClassByStudyDateShouldNotReturnNull() {
        List<ClassSchedule> classSchedules = classScheduleRepository.findFilterActiveClassByStudyDate(
                null,
                ClassStatus.OPENNING.toString(),
                null,
                Arrays.asList("abc", "xyz", "123"),
                Arrays.asList("className1", "className2"),
                Arrays.asList("Ho Chi Minh", "Ha Noi")
        );
        assertNotNull(classSchedules);
        assertEquals(0, classSchedules.size());
    }

    @Test
    void findFilterActiveClassByStudyDateBetweenShouldWork() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        List<String> className = new ArrayList<>();
        className.add("className1");
        className.add("className3");
        List<String> classCode = new ArrayList<>();
        classCode.add("classCode1");
        classCode.add("classCode2");
        List<String> city = new ArrayList<>();
        city.add("city1");
        city.add("city2");

        List<ClassSchedule> classSchedules = classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                startDate,
                endDate,
                ClassStatus.OPENNING.toString(),
                null,
                className,
                classCode,
                city
        );
        assertNotNull(classSchedules);
    }

    @Test
    void findFilterActiveClassByStudyDateBetweenShouldNotReturnNull() {
        LocalDate startDate = LocalDate.of(2023, 3, 20);
        LocalDate endDate = LocalDate.of(2023, 3, 12);

        List<ClassSchedule> classSchedules = classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                startDate,
                endDate,
                ClassStatus.OPENNING.toString(),
                null,
                null,
                Arrays.asList("className1", "className2"),
                Arrays.asList("Ho Chi Minh", "Ha Noi")
        );
        assertNotNull(classSchedules);
        assertEquals(0, classSchedules.size());
    }

}
