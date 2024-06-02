package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.Location;
import com.fptacademy.training.service.dto.LocationDto;
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


@ExtendWith(MockitoExtension.class)
class LocationMapperTest {

    @Mock
    private ModelMapper modelMapper;
    private LocationMapper locationMapper;
    @BeforeEach
    void setUp() {
        locationMapper = new LocationMapper(modelMapper);
    }

    @Test
    void shouldConvertToDtoWithNotNullLocation() {
        //given
        Location location = new Location();
        location.setId(1L);
        location.setCity("city");
        location.setFsu("fsu");
        location.setCode("code");
        //when
        LocationDto locationDto = locationMapper.toDto(location);
        //then
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        Mockito.verify(modelMapper).map(locationArgumentCaptor.capture(), Mockito.eq(LocationDto.class));
        Location capturedLocation = locationArgumentCaptor.getValue();
        Assertions.assertThat(capturedLocation).isEqualTo(location);
    }

    @Test
    void shouldNotConvertToDtoWithNullAttendee() {
        //given
        Location location = null;
        //when
        LocationDto locationDto = locationMapper.toDto(location);
        //then
        Mockito.verify(modelMapper, Mockito.never()).map(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertThat(locationDto).isNull();
    }
}