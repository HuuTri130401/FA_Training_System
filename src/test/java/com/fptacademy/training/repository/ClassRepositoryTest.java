package com.fptacademy.training.repository;

import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.ClassDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClassRepositoryTest {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassDetailRepository classDetailRepository;
    private Long class1_id;
    private Long class2_id;

    @BeforeEach
    void setUp() {
        Class class1 = Class.builder()
                .code("classCode-1")
                .duration(10)
                .name("className-1")
                .build();
        ClassDetail classDetail1 = ClassDetail.builder()
                .classField(class1)
                .status("OPENNING")
                .build();
        class1_id = classRepository.save(class1).getId();
        classDetailRepository.save(classDetail1);

        Class class2 = Class.builder()
                .code("classCode-2")
                .duration(10)
                .name("className-2")
                .build();
        ClassDetail classDetail2 = ClassDetail.builder()
                .classField(class2)
                .status("DELETED")
                .build();
        class2_id = classRepository.save(class2).getId();
        classDetailRepository.save(classDetail2);
    }

    @AfterEach
    void tearDown() {
        classRepository.deleteAll();
    }

    @Test
    void findByExistedIdAndStatusNotDeletedShouldNotReturnNull() {
        //given
        Long id = class1_id;
        Class testClass = Class.builder()
                .code("classCode-1")
                .duration(10)
                .name("className-1")
                .classDetail(ClassDetail.builder().status("OPENNING").build())
                .build();
        //when
        Optional<Class> isExistClass = classRepository.findByIdAndStatusNotDeleted(id);
        //then
        Assertions.assertThat(isExistClass)
                .isPresent()
                .hasValueSatisfying(c -> {
                    Assertions.assertThat(c).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "lastModifiedAt", "classDetail")
                            .isEqualTo(testClass);
                });
    }

    @Test
    void findByExistedIdAndStatusDeletedShouldReturnNull() {
        //given
        Long id = class2_id;
        //when
        Optional<Class> isExistClass = classRepository.findByIdAndStatusNotDeleted(id);
        //then
        Assertions.assertThat(isExistClass)
                .isEmpty();
    }
}