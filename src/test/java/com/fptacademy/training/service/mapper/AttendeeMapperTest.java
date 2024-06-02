package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.Attendee;
import com.fptacademy.training.service.dto.AttendeeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class AttendeeMapperTest {

    @Mock
    private ModelMapper modelMapper;
    private AttendeeMapper attendeeMapper;
    @BeforeEach
    void setUp() {
        attendeeMapper = new AttendeeMapper(modelMapper);
    }

    @Test
    void shouldConvertToDtoWithNotNullAttendee() {
        //given
        Attendee attendee = new Attendee();
        attendee.setId(1L);
        attendee.setType("Fresher");
        attendee.setCode("FR");
        //when
        AttendeeDto attendeeDto = attendeeMapper.toDto(attendee);
        //then
        ArgumentCaptor<Attendee> attendeeArgumentCaptor = ArgumentCaptor.forClass(Attendee.class);
        Mockito.verify(modelMapper).map(attendeeArgumentCaptor.capture(), Mockito.eq(AttendeeDto.class));
        Attendee capturedAttendee = attendeeArgumentCaptor.getValue();
        Assertions.assertThat(capturedAttendee).isEqualTo(attendee);
    }

    @Test
    void shouldNotConvertToDtoWithNullAttendee() {
        //given
        Attendee attendee = null;
        //when
        AttendeeDto attendeeDto = attendeeMapper.toDto(attendee);
        //then
        Mockito.verify(modelMapper, Mockito.never()).map(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertThat(attendeeDto).isNull();
    }}