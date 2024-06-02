package com.fptacademy.training.service.mapper;

import com.fptacademy.training.domain.Unit;
import com.fptacademy.training.service.dto.ReturnUnitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitMapper {

    public ReturnUnitDto toDto(Unit unit) {
        if (unit == null)
            return null;
        ReturnUnitDto result = new ReturnUnitDto();
        result.setId(unit.getId());
        result.setName(unit.getName());
        result.setTitle(unit.getTitle());
        result.setIndex(unit.getIndex());
        return result;
    }

    public List<ReturnUnitDto> toListDto(List<Unit> units) {
        if (units == null) {
            return null;
        }
        List<ReturnUnitDto> result = new ArrayList<>();
        units.forEach(unit -> {
            ReturnUnitDto tmp = toDto(unit);
            result.add(tmp);
        });
        return result;
    }


}
