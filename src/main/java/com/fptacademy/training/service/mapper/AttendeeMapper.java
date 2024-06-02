package com.fptacademy.training.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fptacademy.training.domain.Attendee;
import com.fptacademy.training.service.dto.AttendeeDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AttendeeMapper {

    private final ModelMapper modelMapper;

    public AttendeeDto toDto(Attendee attendee){
        if(attendee == null){
            return null;
        }

        AttendeeDto dto = modelMapper.map(attendee, AttendeeDto.class);
        return dto;
    }
}
