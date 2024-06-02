package com.fptacademy.training.service;

import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.ClassDetail;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.exception.ResourceNotFoundException;
import com.fptacademy.training.repository.ClassDetailRepository;
import com.fptacademy.training.repository.ClassRepository;
import com.fptacademy.training.service.mapper.ClassDetailMapper;
import com.fptacademy.training.service.mapper.ClassMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;
    @Mock
    private ClassDetailRepository classDetailRepository;
    @Mock
    private ClassMapper classMapper;
    @Mock
    private ClassDetailMapper classDetailMapper;
    @InjectMocks
    private ClassService classService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getByExistedIdShouldReturn() {
        //given
        Class testClass = new Class();
        testClass.setId(1L);
        Mockito.when(classRepository.findByIdAndStatusNotDeleted(testClass.getId()))
                .thenReturn(Optional.of(testClass));
        //when
        classService.getById(testClass.getId());
        //then
        ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);
        Mockito.verify(classMapper).toDto(classArgumentCaptor.capture());
        Class capturedClass = classArgumentCaptor.getValue();
        Assertions.assertThat(capturedClass).isEqualTo(testClass);
    }

    @Test
    void getByNotExistedIdShouldThrowException() {
        //given
        Long classId = 1L;
        Mockito.when(classRepository.findByIdAndStatusNotDeleted(classId))
                .thenReturn(Optional.empty());
        //when
        //then
        Assertions.assertThatThrownBy(() -> classService.getById(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class ID " + classId + " not found");
        Mockito.verify(classMapper, Mockito.never()).toDto(ArgumentMatchers.any());
    }

    @Test
    void getDetailsByExistedClass_IdShouldReturn() {
        //given
        Class testClass = new Class();
        testClass.setId(1L);
        ClassDetail testClassDetail = new ClassDetail();
        testClassDetail.setClassField(testClass);
        Mockito.when(classDetailRepository.findDetailsByClass_IdAndStatusNotDeleted(testClass.getId()))
                .thenReturn(Optional.of(testClassDetail));
        //when
        classService.getDetailsByClass_Id(testClass.getId());
        //then
        ArgumentCaptor<ClassDetail> classDetailArgumentCaptor = ArgumentCaptor.forClass(ClassDetail.class);
        Mockito.verify(classDetailMapper).toDto(classDetailArgumentCaptor.capture());
        ClassDetail capturedClassDetail = classDetailArgumentCaptor.getValue();
        Assertions.assertThat(capturedClassDetail).isEqualTo(testClassDetail);
    }

    @Test
    void getDetailsByNotExistedClass_IdShouldThrowException() {
        //given
        Long classId = 1L;
        Mockito.when(classDetailRepository.findDetailsByClass_IdAndStatusNotDeleted(classId))
                .thenReturn(Optional.empty());
        //when
        //then
        Assertions.assertThatThrownBy(() -> classService.getDetailsByClass_Id(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class ID " + classId + " not found");
        Mockito.verify(classDetailMapper, Mockito.never()).toDto(ArgumentMatchers.any());
    }

    @Test
    void shouldDeleteExistedClass() {
        //given
        Class testClass = new Class();
        testClass.setId(1L);
        ClassDetail testClassDetail = new ClassDetail();
        testClassDetail.setClassField(testClass);
        testClassDetail.setStatus("OPENNING");
        testClass.setClassDetail(testClassDetail);
        Mockito.when(classRepository.findById(testClass.getId()))
                .thenReturn(Optional.of(testClass));
        //when
        classService.deleteClass(testClass.getId());
        //then
        Assertions.assertThat(testClassDetail.getStatus()).isEqualTo(ClassStatus.DELETED.toString());
    }

    @Test
    void deleteNotExistedClassShouldThrowException() {
        //given
        Long classId = 1L;
        Mockito.when(classRepository.findById(classId))
                .thenReturn(Optional.empty());
        //when
        //then
        Assertions.assertThatThrownBy(() -> classService.deleteClass(classId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class ID " + classId + " not found");
    }
}