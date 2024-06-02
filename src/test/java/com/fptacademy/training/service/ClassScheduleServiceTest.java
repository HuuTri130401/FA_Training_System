package com.fptacademy.training.service;

import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.ClassScheduleRepository;
import com.fptacademy.training.service.util.DateTimeUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassScheduleServiceTest {

    @Mock
    private ClassScheduleRepository classScheduleRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ClassScheduleService classScheduleService;
    private List<ClassSchedule> classSchedules;
    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setFullName("Im current user");

        User trainer;
        trainer = new User();
        trainer.setFullName("Dao Minh Tri");
        trainer.setId(1L);

        ClassSchedule classSchedule1, classSchedule2;

        classSchedule1 = new ClassSchedule();
        classSchedule1.setId(1L);
        classSchedule1.setStudyDate(LocalDate.of(2023, 3, 17));
        classSchedule1.setTrainer(trainer);

        classSchedule2 = new ClassSchedule();
        classSchedule2.setId(1L);
        classSchedule2.setStudyDate(LocalDate.of(2023, 3, 17));
        classSchedule2.setTrainer(trainer);

        classSchedules = new ArrayList<>();
        classSchedules.add(classSchedule1);
        classSchedules.add(classSchedule2);
    }

    @Test
    void testGetFilterClassScheduleByDateShouldReturnAList() {
        //given
        LocalDate date = LocalDate.of(2023, 3, 27);
        given(classScheduleRepository.findFilterActiveClassByStudyDate(
                any(LocalDate.class),
                eq(ClassStatus.OPENNING.toString()),
                eq(null),
                eq(null),
                eq(null),
                eq(null)))
                .willReturn(classSchedules);
        //when
        List<ClassSchedule> result = classScheduleService.getFilterClassScheduleByDate(
                date,
                null,
                null,
                null
        );
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(classScheduleRepository, times(1)).findFilterActiveClassByStudyDate(
                date,
                ClassStatus.OPENNING.toString(),
                null,
                null,
                null,
                null
        );
    }

    @Test
    void testGetFilterClassScheduleByDateShouldThrowException() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(
                        () -> classScheduleService.getFilterClassScheduleByDate(
                                null,
                                null,
                                null,
                                null
                        )
                )
                .hasMessage("Bad request: date is null")
                .isInstanceOf(ResourceBadRequestException.class);

        verify(classScheduleRepository, never()).findFilterActiveClassByStudyDate(
                null,
                ClassStatus.OPENNING.toString(),
                null,
                null,
                null,
                null
        );
    }

    @Test
    void testGetFilterClassScheduleOfCurrentUserByDateShouldReturnAList() {
        //given
        LocalDate date = LocalDate.of(2023, 3, 29);
        given(userService.getCurrentUserLogin()).willReturn(currentUser);
        given(classScheduleRepository.findFilterActiveClassByStudyDate(
                any(LocalDate.class),
                eq(ClassStatus.OPENNING.toString()),
                anyLong(),
                eq(null),
                eq(null),
                eq(null)
        )).willReturn(classSchedules);
        //when
        List<ClassSchedule> result = classScheduleService.getFilterClassScheduleOfCurrentUserByDate(
                date,
                null,
                null,
                null
        );
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(classScheduleRepository).findFilterActiveClassByStudyDate(
                date,
                ClassStatus.OPENNING.toString(),
                1L,
                null,
                null,
                null
        );
    }

    @Test
    void testGetFilterClassScheduleOfCurrentUserByDateShouldThrowException() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(
                        () -> classScheduleService.getFilterClassScheduleOfCurrentUserByDate(
                                null,
                                null,
                                null,
                                null
                        )
                )
                .hasMessage("Bad request: date is null")
                .isInstanceOf(ResourceBadRequestException.class);

        verify(userService, never()).getCurrentUserLogin();
        verify(classScheduleRepository, never()).findFilterActiveClassByStudyDate(
                null,
                ClassStatus.OPENNING.toString(),
                null,
                null,
                null,
                null
        );
    }


    @Test
    void testGetFilterClassScheduleOfCurrentUserInAWeekShouldReturnAList() {
        //given
        LocalDate date = LocalDate.of(2023, 3, 29);
        given(userService.getCurrentUserLogin()).willReturn(currentUser);
        given(classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                any(LocalDate.class),
                any(LocalDate.class),
                eq(ClassStatus.OPENNING.toString()),
                anyLong(),
                eq(null),
                eq(null),
                eq(null)
        )).willReturn(classSchedules);
        //when
        List<ClassSchedule> result = classScheduleService.getFilterClassScheduleOfCurrentUserInAWeek(
                date,
                null,
                null,
                null
        );
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(classScheduleRepository).findFilterActiveClassByStudyDateBetween(
                LocalDate.of(2023, 3, 27),
                LocalDate.of(2023, 4, 2),
                ClassStatus.OPENNING.toString(),
                1L,
                null,
                null,
                null
        );
    }

    @Test
    void testGetFilterClassScheduleOfCurrentUserInAWeekShouldThrowException() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(
                        () -> classScheduleService.getFilterClassScheduleOfCurrentUserInAWeek(
                                null,
                                null,
                                null,
                                null
                        )
                )
                .hasMessage("Bad request: date is null")
                .isInstanceOf(ResourceBadRequestException.class);
        verify(userService, never()).getCurrentUserLogin();
        verify(classScheduleRepository, never()).findFilterActiveClassByStudyDateBetween(
                any(LocalDate.class),
                any(LocalDate.class),
                anyString(),
                anyLong(),
                eq(null),
                eq(null),
                eq(null)
        );
    }

    @Test
    void testGetFilterClassScheduleInAWeekShouldReturnAList() {
        //given
        LocalDate date = LocalDate.of(2023, 3, 29);
        LocalDate firstDate = DateTimeUtil.getFirstDateOfCurrentWeek(date);
        LocalDate lastDate = DateTimeUtil.getLastDateOfCurrentWeek(date);
        given(classScheduleRepository.findFilterActiveClassByStudyDateBetween(
                any(LocalDate.class),
                any(LocalDate.class),
                eq(ClassStatus.OPENNING.toString()),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        )).willReturn(classSchedules);
        //when
        List<ClassSchedule> result = classScheduleService.getFilterClassScheduleInAWeek(
                date,
                null,
                null,
                null
        );
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(classScheduleRepository).findFilterActiveClassByStudyDateBetween(
                firstDate,
                lastDate,
                ClassStatus.OPENNING.toString(),
                null,
                null,
                null,
                null
        );
    }

    @Test
    void testGetFilterClassScheduleInAWeekShouldThrowException() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(
                        () -> classScheduleService.getFilterClassScheduleInAWeek(
                                null,
                                null,
                                null,
                                null
                        )
                )
                .hasMessage("Bad request: date is null.")
                .isInstanceOf(ResourceBadRequestException.class);
        verify(classScheduleRepository, never()).findFilterActiveClassByStudyDateBetween(
                any(LocalDate.class),
                any(LocalDate.class),
                anyString(),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        );
    }

    @Test
    @DisplayName("Test getCurrentClassDay case 1")
    void getCurrentClassDayShouldReturnAnInteger() {
        //given
        given(classScheduleRepository.getCurrentClassDayOfClassSchedule(anyLong(), anyLong()))
                .willReturn(3);
        //when
        Integer result = classScheduleService.getCurrentClassDay(anyLong(), anyLong());
        //then
        assertEquals(3, result);
        verify(classScheduleRepository).getCurrentClassDayOfClassSchedule(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Test getCurrentClassDay case 2")
    void getCurrentClassDayShouldThrowException() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> classScheduleService.getCurrentClassDay(null, 1L)
        )
                .hasMessage("Bad request for classId and classScheduleId")
                .isInstanceOf(ResourceBadRequestException.class);

        assertThatThrownBy(
                () -> classScheduleService.getCurrentClassDay(1L, null)
        )
                .hasMessage("Bad request for classId and classScheduleId")
                .isInstanceOf(ResourceBadRequestException.class);

        verify(classScheduleRepository, never()).getCurrentClassDayOfClassSchedule(null, 1L);
    }

    @Test
    @DisplayName("Test getCurrentClassDay case 3")
    void getCurrentClassDayShouldReturnDefaultValue() {
        //given
        given(classScheduleRepository.getCurrentClassDayOfClassSchedule(anyLong(), anyLong()))
                .willReturn(null);
        //when
        Integer result = classScheduleService.getCurrentClassDay(anyLong(), anyLong());
        //then
        assertEquals(-1, result);

        verify(classScheduleRepository).getCurrentClassDayOfClassSchedule(anyLong(), anyLong());
    }
}