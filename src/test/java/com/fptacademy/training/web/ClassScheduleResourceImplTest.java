package com.fptacademy.training.web;

import com.fptacademy.training.service.ClassScheduleService;
import com.fptacademy.training.service.dto.ReturnClassScheduleDto;
import com.fptacademy.training.service.dto.ReturnUnitDto;
import com.fptacademy.training.service.mapper.ClassScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClassScheduleResourceImplTest {

    @Mock
    private ClassScheduleService classScheduleService;
    @Mock
    private ClassScheduleMapper classScheduleMapper;
    @InjectMocks
    private ClassScheduleResourceImpl classScheduleResource;
    @Autowired
    private MockMvc mockMvc;
    private List<ReturnClassScheduleDto> classScheduleDTOList;

    @BeforeEach
    void setUp() {

        ReturnClassScheduleDto classScheduleDTO1, classScheduleDTO2;
        ReturnUnitDto unitDto1, unitDto2;
        List<ReturnUnitDto> unitDtos;

        unitDto1 = new ReturnUnitDto(1L, 1, "Test unit 1", "Test unit 1");
        unitDto2 = new ReturnUnitDto(2L, 2, "Test unit 2", "Test unit 2");
        unitDtos = new ArrayList<>();
        unitDtos.add(unitDto1);
        unitDtos.add(unitDto2);

        classScheduleDTO1 = new ReturnClassScheduleDto();
        classScheduleDTO1.setClassId(1L);
        classScheduleDTO1.setClassCode("Java01");
        classScheduleDTO1.setClassName("Java intern 01");
        classScheduleDTO1.setDate(LocalDate.now());
        classScheduleDTO1.setType("Intern");
        classScheduleDTO1.setCity("Ho Chi Minh");
        classScheduleDTO1.setFsu("Ftown1");
        classScheduleDTO1.setUnits(unitDtos);

        classScheduleDTO2 = new ReturnClassScheduleDto();
        classScheduleDTO2.setClassId(2L);
        classScheduleDTO2.setClassCode("React01");
        classScheduleDTO2.setClassName("React intern 01");
        classScheduleDTO2.setDate(LocalDate.now());
        classScheduleDTO2.setType("Intern");
        classScheduleDTO1.setCity("Ho Chi Minh");
        classScheduleDTO1.setFsu("Ftown3");
        classScheduleDTO1.setUnits(unitDtos);

        classScheduleDTOList = new ArrayList<>();
        classScheduleDTOList.add(classScheduleDTO1);
        classScheduleDTOList.add(classScheduleDTO2);

        mockMvc = MockMvcBuilders.standaloneSetup(classScheduleResource).build();
    }

    @Test
    void getAllClassScheduleShouldReturnAListOfClassScheduleDTO() throws Exception {
        given(classScheduleService.getFilterClassScheduleByDate(any(LocalDate.class), eq(null), eq(null), eq(null)))
                .willReturn(new ArrayList<>());
        given(classScheduleMapper.toListReturnClassScheduleDto(anyList()))
                .willReturn(classScheduleDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/calendar?date=2023-03-27")
//                        .header("Authorization", token)
//                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("Java01"))
                .andExpect(jsonPath("$[1].classCode").value("React01"))
                .andExpect(jsonPath("$[0].classId").value("1"))
                .andExpect(jsonPath("$[1].classId").value("2"))
                .andExpect(jsonPath("$[0].className").value("Java intern 01"))
                .andExpect(jsonPath("$[1].className").value("React intern 01"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(classScheduleService).getFilterClassScheduleByDate(any(LocalDate.class), eq(null), eq(null), eq(null));
        verify(classScheduleMapper).toListReturnClassScheduleDto(anyList());
    }

    @Test
    void getAllClassScheduleByWeekShouldReturnAList() throws Exception {
        given(classScheduleService.getFilterClassScheduleInAWeek(any(LocalDate.class), eq(null), eq(null), eq(null)))
                .willReturn(new ArrayList<>());
        given(classScheduleMapper.toListReturnClassScheduleDto(anyList()))
                .willReturn(classScheduleDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/calendar/week?date=2023-03-27")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("Java01"))
                .andExpect(jsonPath("$[1].classCode").value("React01"))
                .andExpect(jsonPath("$[0].classId").value("1"))
                .andExpect(jsonPath("$[1].classId").value("2"))
                .andExpect(jsonPath("$[0].className").value("Java intern 01"))
                .andExpect(jsonPath("$[1].className").value("React intern 01"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(classScheduleService).getFilterClassScheduleInAWeek(any(LocalDate.class), eq(null), eq(null), eq(null));
        verify(classScheduleMapper).toListReturnClassScheduleDto(anyList());
    }

    @Test
    void getAllClassScheduleOfCurrentUserShouldReturnAList() throws Exception {
        given(classScheduleService.getFilterClassScheduleOfCurrentUserByDate(any(LocalDate.class), eq(null), eq(null), eq(null)))
                .willReturn(new ArrayList<>());
        given(classScheduleMapper.toListReturnClassScheduleDto(anyList()))
                .willReturn(classScheduleDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/calendar?date=2023-03-27")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("Java01"))
                .andExpect(jsonPath("$[1].classCode").value("React01"))
                .andExpect(jsonPath("$[0].classId").value("1"))
                .andExpect(jsonPath("$[1].classId").value("2"))
                .andExpect(jsonPath("$[0].className").value("Java intern 01"))
                .andExpect(jsonPath("$[1].className").value("React intern 01"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(classScheduleService).getFilterClassScheduleOfCurrentUserByDate(any(LocalDate.class), eq(null), eq(null), eq(null));
        verify(classScheduleMapper).toListReturnClassScheduleDto(anyList());
    }

    @Test
    void getAllClassScheduleOfCurrentUserByWeekShouldReturnAList() throws Exception {
        given(classScheduleService.getFilterClassScheduleOfCurrentUserInAWeek(any(LocalDate.class), eq(null), eq(null), eq(null)))
                .willReturn(new ArrayList<>());
        given(classScheduleMapper.toListReturnClassScheduleDto(anyList()))
                .willReturn(classScheduleDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/calendar/week?date=2023-03-27")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("Java01"))
                .andExpect(jsonPath("$[1].classCode").value("React01"))
                .andExpect(jsonPath("$[0].classId").value("1"))
                .andExpect(jsonPath("$[1].classId").value("2"))
                .andExpect(jsonPath("$[0].className").value("Java intern 01"))
                .andExpect(jsonPath("$[1].className").value("React intern 01"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(classScheduleService).getFilterClassScheduleOfCurrentUserInAWeek(any(LocalDate.class), eq(null), eq(null), eq(null));
        verify(classScheduleMapper).toListReturnClassScheduleDto(anyList());
    }
}