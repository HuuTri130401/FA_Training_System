package com.fptacademy.training.web;

import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.ClassDetail;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.ClassDetailRepository;
import com.fptacademy.training.repository.ClassRepository;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.repository.UserRepository;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.security.jwt.JwtTokenProvider;
import com.fptacademy.training.service.mapper.ClassMapper;
import com.fptacademy.training.service.util.TestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ClassResourceImplTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private ClassDetailRepository classDetailRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private MockMvc mockMvc;
    private String accessToken;
    private Long notExistClassId = 0L;
    private Long class1_id;

    @BeforeEach
    void setUp() {
        Role role = RoleFactory.createRoleWithPermissions(Permissions.CLASS_FULL_ACCESS);
        roleRepository.saveAndFlush(role);
        User user = UserFactory.createActiveUser(role);
        userRepository.saveAndFlush(user);
        Authentication authentication = TestUtil.createAuthentication(user);
        accessToken = tokenProvider.generateAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        classDetailRepository.deleteAll();
        classRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getClassByExistedIdShouldResponseOK() throws Exception {
        //given
        Long classId = class1_id;
        Class testClass = Class.builder()
                .code("classCode-1")
                .duration(10)
                .name("className-1")
                .build();
        ClassDetail testClassDetail1 = ClassDetail.builder()
                .classField(testClass)
                .status("OPENNING")
                .build();
        testClass.setClassDetail(testClassDetail1);

        //when
        String getByIdUrl = "/api/class/{class_id}";
        ResultActions resultActions = mockMvc.perform(get(getByIdUrl, classId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
        //then
        resultActions
                .andExpect(status().isOk());

        //...
    }

    @Test
    void getClassByNotExistedIdShouldResponseNotFound() throws Exception {
        //given
        Long classId = notExistClassId;

        //when
        String getByIdUrl = "/api/class/{class_id}";
        ResultActions resultActions = mockMvc.perform(get(getByIdUrl, classId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
        //then
        resultActions.andExpect(status().isNotFound());

        //...
    }

    @Test
    void delExistedClassShouldReturnOk() throws Exception {
        //given
        Long classId = class1_id;
        //when
        String deleteByIdUrl = "/api/class/{id}";
        ResultActions resultActions = mockMvc.perform(
                delete(deleteByIdUrl, classId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
        //then
        resultActions.andExpect(status().isOk());
        Class savedClass = classRepository.findById(classId).get();
        Assertions.assertThat(savedClass.getClassDetail().getStatus()).isEqualTo(ClassStatus.DELETED.toString());
    }

    @Test
    void delNotExistedClassShouldReturnNotFound() throws Exception {
        //given
        Long classId = notExistClassId;
        //when
        String deleteByIdUrl = "/api/class/{id}";
        ResultActions resultActions = mockMvc.perform(
                delete(deleteByIdUrl, classId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
        //then
        resultActions.andExpect(status().isNotFound());
    }
}