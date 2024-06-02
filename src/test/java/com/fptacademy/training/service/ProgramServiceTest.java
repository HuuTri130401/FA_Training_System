package com.fptacademy.training.service;

import com.fptacademy.training.domain.*;
import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.exception.ResourceAlreadyExistsException;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.exception.ResourceNotFoundException;
import com.fptacademy.training.factory.ProgramFactory;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.SyllabusFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.*;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.dto.ProgramDto;
import com.fptacademy.training.service.dto.SyllabusDto;
import com.fptacademy.training.service.util.TestUtil;
import com.fptacademy.training.web.vm.ProgramVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ProgramServiceTest {
    private final String DEFAULT_PROGRAM_NAME = "Test Program";
    private User user;
    @Autowired
    private ProgramService programService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SyllabusRepository syllabusRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private ClassRepository classRepository;

    @BeforeEach
    void setup() {
        Role role = RoleFactory.createRoleWithPermissions(Permissions.PROGRAM_FULL_ACCESS);
        roleRepository.saveAndFlush(role);
        user = UserFactory.createActiveUser(role);
        userRepository.saveAndFlush(user);
        Authentication authentication = TestUtil.createAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
        classRepository.deleteAll();
        programRepository.deleteAll();
        syllabusRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
    @Test
    void testCreateProgram() {
        List<Syllabus> syllabuses = List.of(SyllabusFactory.createActivatedDummySyllabus(),
                SyllabusFactory.createActivatedDummySyllabus());
        syllabusRepository.saveAllAndFlush(syllabuses);
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, syllabuses.stream().map(Syllabus::getId).toList());
        ProgramDto programDTO = programService.createProgram(programVM);
        assertThat(programDTO).isNotNull();
        assertThat(programDTO.getName()).isEqualTo(DEFAULT_PROGRAM_NAME);
        assertThat(programDTO.getCreatedBy().getId()).isEqualTo(user.getId());
        assertThat(programDTO.getLastModifiedBy().getId()).isEqualTo(user.getId());
        assertThat(programDTO.getDurationInDays()).isEqualTo(syllabuses.stream().mapToInt(Syllabus::getDuration).sum());
    }

    @Test
    void testCreateProgramWithExistedName() {
        Program program = ProgramFactory.createDummyProgram();
        program.setName(DEFAULT_PROGRAM_NAME);
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        List<Syllabus> syllabuses = List.of(SyllabusFactory.createDummySyllabus(),
                SyllabusFactory.createDummySyllabus());
        syllabusRepository.saveAllAndFlush(syllabuses);
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, syllabuses.stream().map(Syllabus::getId).toList());
        assertThatExceptionOfType(ResourceAlreadyExistsException.class)
                .isThrownBy(() -> programService.createProgram(programVM));
    }

    @Test
    void testDeleteProgram() {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        programService.deleteProgram(program.getId());
        assertThat(programRepository.findById(program.getId())).isNotPresent();
    }

    @Test
    void testDeleteProgramFails() {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        com.fptacademy.training.domain.Class c = Class.builder()
                .name("className")
                .code("ABC123")
                .program(program)
                .build();
        ClassDetail classDetail = ClassDetail.builder()
                .classField(c)
                .status(ClassStatus.OPENNING.name())
                .build();
        c.setClassDetail(classDetail);
        classRepository.saveAndFlush(c);

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> programService.deleteProgram(999L));

        assertThatExceptionOfType(ResourceBadRequestException.class)
                .isThrownBy(() -> programService.deleteProgram(program.getId()));
    }

    @Test
    void testFindSyllabusesByProgramId() {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        List<SyllabusDto.SyllabusListDto> syllabuses = programService.findSyllabusesByProgramId(program.getId());
        assertThat(syllabuses.size()).isEqualTo(program.getSyllabuses().size());
        assertThat(syllabuses.stream()
                .anyMatch(s1 -> program.getSyllabuses().stream()
                        .anyMatch(s2 -> s1.getId().equals(s2.getId()))))
                .isTrue();
    }

    @Test
    void testFindSyllabusesByProgramIdFails() {
        Long id = 999L;
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> programService.findSyllabusesByProgramId(id))
                .withMessage("Training program with id '" + id + "' not existed");
    }

    @Test
    void testActivateProgram() {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        programService.activateProgram(program.getId());
        Optional<Program> optionalProgram = programRepository.findById(program.getId());
        assertThat(optionalProgram).isPresent();
        assertThat(optionalProgram.get().getActivated()).isTrue();
    }
}