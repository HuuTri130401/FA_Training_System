package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.*;
import com.fptacademy.training.domain.enumeration.RoleName;
import com.fptacademy.training.repository.UserRepository;
import com.fptacademy.training.service.ClassScheduleService;
import com.fptacademy.training.service.UserService;
import com.fptacademy.training.service.dto.ReturnClassScheduleDto;
import com.fptacademy.training.service.dto.ReturnUnitDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassScheduleMapperTest {
    private final ClassSchedule classScheduleMock = mock(ClassSchedule.class);
    private final ClassDetail classDetailMock = mock(ClassDetail.class);
    private final Session sessionMock = mock(Session.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private UnitMapper unitMapper;
    @Mock
    private ClassScheduleService classScheduleService;

    @Mock
    private UserService userService;
    @InjectMocks
    private ClassScheduleMapper classScheduleMapper;

    private Class classField;
    private Location location1/*, location2, location3*/;
    private Attendee attendee1;
    private ReturnUnitDto unit1, unit2/*, unit3, unit4*/;
    private List<ReturnUnitDto> returnUnitDtos;

    @BeforeEach
    void init() {


        location1 = new Location();
        location1.setCity("Hồ Chí Minh");
        location1.setFsu("Ftown1");

//        location2 = new Location();
//        location2.setCity("Hồ Chí Minh");
//        location2.setFsu("Ftown3");
//
//        location3 = new Location();
//        location3.setCity("Hà Nội");
//        location3.setFsu("Headquarter");

        attendee1 = new Attendee();
        attendee1.setType("Intern");

        attendee1 = new Attendee();
        attendee1.setType("Fresher");

        unit1 = new ReturnUnitDto();
        unit1.setId(1L);
        unit1.setName("Test unit 1");
        unit1.setTitle("Test unit 1 title");
        unit1.setIndex(1);

        unit2 = new ReturnUnitDto();
        unit2.setId(2L);
        unit2.setName("Test unit 2");
        unit2.setTitle("Test unit 2 title");
        unit2.setIndex(2);
//
//        unit3 = new ReturnUnitDto();
//        unit3.setId(3L);
//        unit3.setName("Test unit 3");
//        unit3.setTitle("Test unit 3 title");
//        unit3.setIndex(3);
//
//        unit4 = new ReturnUnitDto();
//        unit4.setId(4L);
//        unit4.setName("Test unit 4");
//        unit4.setTitle("Test unit 4 title");
//        unit4.setIndex(4);

        returnUnitDtos = new ArrayList<>();
        returnUnitDtos.add(unit1);
        returnUnitDtos.add(unit2);

        classField = new Class();
        classField.setId(1L);
        classField.setCode("Java01");
        classField.setName("Test class");
        classField.setDuration(10);
    }

    @Test
    @DisplayName("Test toReturnClassScheduleDto case 1")
    void toReturnClassScheduleDtoShouldReturnAClassScheduleDTO() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(classDetailMock);
        given(classScheduleMock.getStudyDate()).willReturn(LocalDate.of(2023, 3, 13));
        given(classScheduleMock.getSession()).willReturn(sessionMock);
        given(unitMapper.toListDto(anyList())).willReturn(returnUnitDtos);
        given(classDetailMock.getClassField()).willReturn(classField);
        given(classDetailMock.getLocation()).willReturn(location1);
        given(classDetailMock.getAttendee()).willReturn(attendee1);
        given(classDetailMock.getStartAt()).willReturn(LocalTime.of(8, 0, 0));
        given(classDetailMock.getFinishAt()).willReturn(LocalTime.of(10, 0, 0));
        given(classScheduleService.getCurrentClassDay(anyLong(), anyLong()))
                .willReturn(4);
        given(userService.getMemberOfClassByRole(anyLong(), any(RoleName.class))).willReturn(new ArrayList<>());
        //when
        ReturnClassScheduleDto result = classScheduleMapper.toReturnClassScheduleDto(classScheduleMock);
        //then

        assertNotNull(result);
        assertEquals(classField.getId(), result.getClassId());
        assertEquals(classField.getCode(), result.getClassCode());
        assertEquals(classField.getName(), result.getClassName());
        assertEquals(classField.getDuration(), result.getDuration());
        assertEquals(4, result.getCurrentClassDay());
        assertEquals(location1.getCity(), result.getCity());
        assertEquals(location1.getFsu(), result.getFsu());
        assertEquals(attendee1.getType(), result.getType());
        assertEquals(LocalDate.of(2023, 3, 13), result.getDate());
        assertEquals(LocalTime.of(8, 0, 0), result.getStartAt());
        assertEquals(LocalTime.of(10, 0, 0), result.getFinishAt());
        assertEquals(unit1.getName(), result.getUnits().get(0).getName());
        assertEquals(unit2.getName(), result.getUnits().get(1).getName());
        assertEquals(2, result.getUnits().size());

        verify(classScheduleMock).getClassDetail();
        verify(classDetailMock).getClassField();
        verify(classDetailMock).getAttendee();
        verify(classScheduleService).getCurrentClassDay(anyLong(), anyLong());
        verify(classScheduleMock, times(2)).getSession();
        verify(classScheduleMock.getSession(), times(1)).getUnits();
        verify(unitMapper).toListDto(anyList());
        verify(classDetailMock).getLocation();
    }

    @Test
    @DisplayName("Test toReturnClassScheduleDto case 2")
    void toReturnClassScheduleDtoShouldReturnNullWhenClassDetailIsNull() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(null);

        //when
        ReturnClassScheduleDto result = classScheduleMapper.toReturnClassScheduleDto(classScheduleMock);

        //then
        assertNull(result);

        verify(classScheduleMock).getClassDetail();
        verify(classDetailMock, never()).getClassField();
        verify(classDetailMock, never()).getAttendee();
        verify(classScheduleService, never()).getCurrentClassDay(anyLong(), anyLong());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Test toReturnClassScheduleDto case 3")
    void toReturnClassScheduleDtoShouldReturnNullWhenClassFieldIsNull() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(classDetailMock);
        given(classDetailMock.getAttendee()).willReturn(attendee1);
        given(classDetailMock.getLocation()).willReturn(location1);
        given(classDetailMock.getClassField()).willReturn(null);

        //when
        ReturnClassScheduleDto result = classScheduleMapper.toReturnClassScheduleDto(classScheduleMock);

        //then
        assertNull(result);

        verify(classScheduleMock).getClassDetail();
        verify(classDetailMock).getClassField();
        verify(classDetailMock).getAttendee();
    }

    @Test
    @DisplayName("Test toReturnClassScheduleDto case 4")
    void toReturnClassScheduleDtoShouldReturnNullWhenAttendeeIsNull() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(classDetailMock);
        given(classDetailMock.getAttendee()).willReturn(null);
        given(classDetailMock.getLocation()).willReturn(location1);
        given(classDetailMock.getClassField()).willReturn(classField);

        //when
        ReturnClassScheduleDto result = classScheduleMapper.toReturnClassScheduleDto(classScheduleMock);

        //then
        assertNull(result);

        verify(classScheduleMock).getClassDetail();
        verify(classDetailMock).getClassField();
        verify(classDetailMock).getAttendee();
    }

    @Test
    @DisplayName("Test toListReturnClassScheduleDto case 1")
    void toListReturnClassScheduleDtoShouldReturnAList() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(classDetailMock);
        given(classScheduleMock.getStudyDate()).willReturn(LocalDate.of(2023, 3, 13));
        given(classScheduleMock.getSession()).willReturn(sessionMock);
        given(unitMapper.toListDto(anyList())).willReturn(returnUnitDtos);
        given(classDetailMock.getClassField()).willReturn(classField);
        given(classDetailMock.getLocation()).willReturn(location1);
        given(classDetailMock.getAttendee()).willReturn(attendee1);
        given(classDetailMock.getStartAt()).willReturn(LocalTime.of(8, 0, 0));
        given(classDetailMock.getFinishAt()).willReturn(LocalTime.of(10, 0, 0));
        given(classScheduleService.getCurrentClassDay(anyLong(), anyLong()))
                .willReturn(4);
        given(userService.getMemberOfClassByRole(anyLong(), any(RoleName.class))).willReturn(new ArrayList<>());
        //when
        List<ClassSchedule> input = new ArrayList<>();
        input.add(classScheduleMock);
        input.add(classScheduleMock);
        input.add(classScheduleMock);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(input);

        //then

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test toListReturnClassScheduleDto case 2")
    void toListReturnClassScheduleDtoShouldReturnNull() {
        //given

        given(classScheduleMock.getClassDetail()).willReturn(null);

        //when
        List<ClassSchedule> input = new ArrayList<>();
        input.add(classScheduleMock);
        input.add(classScheduleMock);
        input.add(classScheduleMock);
        List<ReturnClassScheduleDto> result = classScheduleMapper.toListReturnClassScheduleDto(input);
        //then

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(classScheduleMock, times(3)).getClassDetail();
        verify(classDetailMock, never()).getClassField();
        verify(classDetailMock, never()).getAttendee();
        verify(classScheduleService, never()).getCurrentClassDay(anyLong(), anyLong());
        verify(userRepository, never()).findById(anyLong());
    }

}