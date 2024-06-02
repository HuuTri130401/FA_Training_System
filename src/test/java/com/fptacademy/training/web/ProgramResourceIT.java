package com.fptacademy.training.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fptacademy.training.IntegrationTest;
import com.fptacademy.training.domain.*;
import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.factory.ProgramFactory;
import com.fptacademy.training.factory.RoleFactory;
import com.fptacademy.training.factory.SyllabusFactory;
import com.fptacademy.training.factory.UserFactory;
import com.fptacademy.training.repository.*;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.security.jwt.JwtTokenProvider;
import com.fptacademy.training.service.util.TestUtil;
import com.fptacademy.training.web.vm.ProgramVM;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
@IntegrationTest
public class ProgramResourceIT {
    private String accessToken;
    private final String DEFAULT_PROGRAM_NAME = "Test Program";
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SyllabusRepository syllabusRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ClassRepository classRepository;

    @BeforeEach
    void setup() {
        Role role = RoleFactory.createRoleWithPermissions(Permissions.PROGRAM_FULL_ACCESS);
        roleRepository.saveAndFlush(role);
        User user = UserFactory.createActiveUser(role);
        userRepository.saveAndFlush(user);
        Authentication authentication = TestUtil.createAuthentication(user);
        accessToken = tokenProvider.generateAccessToken(authentication);
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
    void testCreateProgram() throws Exception {
        List<Syllabus> syllabuses = List.of(
                SyllabusFactory.createActivatedDummySyllabus(),
                SyllabusFactory.createActivatedDummySyllabus());
        syllabusRepository.saveAllAndFlush(syllabuses);
        SecurityContextHolder.clearContext();
        List<Long> syllabusIds = syllabuses.stream().mapToLong(Syllabus::getId).boxed().toList();
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, syllabusIds);
        mockMvc
                .perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(programVM))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateProgramWithConflictName() throws Exception {
        Program program = Program.builder().name(DEFAULT_PROGRAM_NAME).build();
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, List.of());
        mockMvc
                .perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(programVM))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateProgramWithNonExistSyllabuses() throws Exception {
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, List.of(99L, 100L, 101L));
        mockMvc
                .perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(programVM))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProgramListWithPagination() throws Exception {
        List<Program> programs = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            Program program = ProgramFactory.createDummyProgram();
            if (i == 10) {
                program.setActivated(true);
            }
            syllabusRepository.saveAllAndFlush(program.getSyllabuses());
            programRepository.saveAndFlush(program);
            programs.add(program);
        }
        SecurityContextHolder.clearContext();
        mockMvc
                .perform(get("/api/programs")
                        .param("page", "2")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(20))
                .andExpect(jsonPath("$.programs").isArray())
                .andExpect(jsonPath("$.programs", Matchers.hasSize(10)))
                .andExpect(jsonPath("$.programs[0].id").value(programs.get(10).getId()));

        mockMvc
                .perform(get("/api/programs")
                        .param("sort", "id,desc")
                        .param("page", "2")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(20))
                .andExpect(jsonPath("$.programs").isArray())
                .andExpect(jsonPath("$.programs", Matchers.hasSize(10)))
                .andExpect(jsonPath("$.programs[0].id").value(programs.get(9).getId()));

        mockMvc
                .perform(get("/api/programs")
                        .param("activated", "true")
                        .param("page", "2")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testDownloadProgramExcelTemplate() throws Exception {
        SecurityContextHolder.clearContext();
        MvcResult mvcResult = mockMvc
                .perform(get("/api/programs/import/template")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertThat(mvcResult.getResponse().getContentType())
                .isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    @Test
    void testCreateProgramsByImportingExcel() throws Exception {
        List<Syllabus> syllabuses = List.of(SyllabusFactory.createActivatedDummySyllabus(),
                SyllabusFactory.createActivatedDummySyllabus());
        syllabusRepository.saveAllAndFlush(syllabuses);
        SecurityContextHolder.clearContext();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (outputStream; Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Programs");

            Row row1 = sheet.createRow(0);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Program ID");
            Cell cell2 = row1.createCell(1);
            cell2.setCellValue("Program Name");
            Cell cell3 = row1.createCell(2);
            cell3.setCellValue("Syllabus Codes");
            Cell cell4 = row1.createCell(3);
            cell4.setCellValue("Activated");

            Row row2 = sheet.createRow(1);
            Cell cell5 = row2.createCell(0);
            Cell cell6 = row2.createCell(1);
            cell6.setCellValue("Example Program Name");
            Cell cell7 = row2.createCell(2);
            cell7.setCellValue(syllabuses.stream().map(Syllabus::getCode).collect(Collectors.joining(",")));
            Cell cell8 = row2.createCell(3);
            cell8.setCellValue(false);
            workbook.write(outputStream);
        }
        mockMvc
                .perform(multipart("/api/programs/import").file("file", outputStream.toByteArray())
                        .param("duplicate", "id")
                        .param("handle", "skip")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.duplicateProgramNames.length()").value(0))
                .andExpect(jsonPath("$.programs.length()").value(1))
                .andExpect(jsonPath("$.programs[0].name").value("Example Program Name"));
    }

    @Test
    public void testDeactivateProgram() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        program.setActivated(true);
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        mockMvc.perform(post("/api/programs/{id}/deactivate", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(program.getId()))
                .andExpect(jsonPath("$.activated").value(false));
    }

    @Test
    public void testDeactivateProgramWithClassStillActive() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        Class c = Class.builder()
                .name("className")
                .code("abc")
                .program(program)
                        .build();
        ClassDetail classDetail = ClassDetail.builder()
                .classField(c)
                .status(ClassStatus.OPENNING.name())
                .build();
        c.setClassDetail(classDetail);
        classRepository.saveAndFlush(c);
        SecurityContextHolder.clearContext();
        mockMvc.perform(post("/api/programs/{id}/deactivate", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeactivateProgramNotFound() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(post("/api/programs/{id}/deactivate", 999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testDeleteProgram() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        mockMvc.perform(delete("/api/programs/{id}", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProgramNotFound() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(delete("/api/programs/{id}", 999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProgramBadRequest() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        Class c = Class.builder()
                .name("className")
                .code("abc")
                .program(program)
                .build();
        ClassDetail classDetail = ClassDetail.builder()
                .classField(c)
                .status(ClassStatus.OPENNING.name())
                .build();
        c.setClassDetail(classDetail);
        classRepository.saveAndFlush(c);
        SecurityContextHolder.clearContext();
        mockMvc.perform(delete("/api/programs/{id}", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetSyllabusesByProgramId() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/programs/{id}/syllabus", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(program.getSyllabuses().size()));
    }

    @Test
    public void testGetSyllabusesByProgramIdNotFound() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/programs/{id}/syllabus", 999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    // thanh tai

    @Test
    public void testGetProgramById() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/programs/{id}", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(program.getId()));

    }
    @Test
    public void testGetProgramByIdNotFound() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/programs/{id}", 999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
    // thanh tai

    @Test
    public void testActivateProgram() throws Exception {
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/programs/{id}/activate", program.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(program.getId()))
                .andExpect(jsonPath("$.activated").value(true));

    }

    @Test
    public void testActivateProgramNotFound() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(post("/api/programs/{id}/activate", 999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProgram() throws Exception{
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        List<Syllabus> syllabusList = List.of(SyllabusFactory.createActivatedDummySyllabus(),
                SyllabusFactory.createActivatedDummySyllabus());
        syllabusRepository.saveAllAndFlush(syllabusList);
        SecurityContextHolder.clearContext();
        List<Long> syllabusIds = syllabusList.stream().mapToLong(Syllabus::getId).boxed().toList();
        ProgramVM programVM = new ProgramVM("Update Program Name", syllabusIds);
        mockMvc.perform(put("/api/programs/{id}", program.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(programVM))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Update Program Name"));

    }

    @Test
    public void testUpdateProgramIdNotFound() throws Exception{
        SecurityContextHolder.clearContext();
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, List.of(99L));
        mockMvc.perform(put("/api/programs/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(programVM))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProgramWithNonExistSyllabuses() throws Exception{
        Program program = ProgramFactory.createDummyProgram();
        syllabusRepository.saveAllAndFlush(program.getSyllabuses());
        programRepository.saveAndFlush(program);
        SecurityContextHolder.clearContext();
        ProgramVM programVM = new ProgramVM(DEFAULT_PROGRAM_NAME, List.of(99L));
        mockMvc.perform(put("/api/programs/{id}", program.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(TestUtil.convertObjectToJsonBytes(programVM))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetSyllabusesByName() throws Exception {
        Syllabus syllabus1=SyllabusFactory.createActivatedDummySyllabus();
        syllabus1.setName("syllabus 1");
        Syllabus syllabus2=SyllabusFactory.createActivatedDummySyllabus();
        syllabus2.setName("syllabus 2");
        Syllabus syllabus3=SyllabusFactory.createActivatedDummySyllabus();
        syllabus3.setName("syllabus 3");
        List<Syllabus> syllabusList=List.of(syllabus1,
                syllabus2,
                syllabus3);
        syllabusRepository.saveAllAndFlush(syllabusList);
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/syllabuses/search")
                        .param("name","syllabus")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/syllabuses/search")
                        .param("name","abc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}