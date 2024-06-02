package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.Program;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.Syllabus;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.factory.ProgramFactory;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.ProgramRepository;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.repository.SyllabusRepository;
import com.fptacademy.training.repository.UserRepository;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.dto.ProgramDto;
import com.fptacademy.training.service.util.TestUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ProgramMapperTest {
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private SyllabusRepository syllabusRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProgramMapper programMapper;

    @BeforeEach
    void setup() {
        Role role = RoleFactory.createRoleWithPermissions(Permissions.PROGRAM_FULL_ACCESS);
        roleRepository.saveAndFlush(role);
        User user = UserFactory.createActiveUser(role);
        userRepository.saveAndFlush(user);
        Authentication authentication = TestUtil.createAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
        programRepository.deleteAll();
        syllabusRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testConvertEntityToDto() {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        ProgramDto programDTO = programMapper.toDto(program);
        assertThat(programDTO.getId()).isEqualTo(program.getId());
        assertThat(programDTO.getName()).isEqualTo(program.getName());
        assertThat(programDTO.getCreatedBy().getId()).isEqualTo(program.getCreatedBy().getId());
        assertThat(programDTO.getLastModifiedBy().getId()).isEqualTo(program.getLastModifiedBy().getId());
        assertThat(programDTO.getCreatedAt()).isEqualTo(program.getCreatedAt());
        assertThat(programDTO.getLastModifiedAt()).isEqualTo(program.getLastModifiedAt());
        assertThat(programDTO.getDurationInDays()).isEqualTo(program.getSyllabuses().stream().mapToInt(Syllabus::getDuration).sum());
    }

    @Test
    void testConvertNullEntityToDto() {
        ProgramDto programDTO = programMapper.toDto(null);
        assertThat(programDTO).isNull();

        List<ProgramDto> programDTOs = programMapper.toDtos(null);
        assertThat(programDTOs).isNull();

        Program program1 = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program1.getSyllabuses());
        programRepository.saveAndFlush(program1);
        Program program2 = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program2.getSyllabuses());
        programRepository.saveAndFlush(program2);
        List<Program> programs = new ArrayList<>();
        programs.add(program1);
        programs.add(null);
        programs.add(program2);
        programDTOs = programMapper.toDtos(programs);
        assertThat(programDTOs.size()).isEqualTo(2);
    }
}