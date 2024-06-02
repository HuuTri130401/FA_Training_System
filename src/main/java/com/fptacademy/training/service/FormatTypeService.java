package com.fptacademy.training.service;

import com.fptacademy.training.domain.FormatType;
import com.fptacademy.training.repository.FormatTypeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FormatTypeService {

  private final FormatTypeRepository formatTypeRepository;
  private final ModelMapper modelMapper;

  @Transactional
  public List<FormatType> getAll() {
    return formatTypeRepository.findAll();
  }

  @Transactional
  public Optional<FormatType> getOne(Long id) {
    return formatTypeRepository.findById(id);
  }

  public FormatType save(FormatType formatType) {
    return formatTypeRepository.save(formatType);
  }

  public Optional<FormatType> update(FormatType result) {
    return formatTypeRepository
      .findById(result.getId())
      .map(ops -> {
        modelMapper.map(result, ops);
        return ops;
      })
      .map(formatTypeRepository::save);
  }

  public void delete(Long id) {
    formatTypeRepository.deleteById(id);
  }
}
