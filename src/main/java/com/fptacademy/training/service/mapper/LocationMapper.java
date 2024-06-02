package com.fptacademy.training.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.fptacademy.training.domain.Location;
import com.fptacademy.training.service.dto.LocationDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LocationMapper {

    private final ModelMapper modelMapper;

    public LocationDto toDto(Location location){
        if(location == null){
            return null;
        }

        LocationDto dto = modelMapper.map(location, LocationDto.class);
        return dto;
    }
}
