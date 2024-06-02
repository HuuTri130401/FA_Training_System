package com.fptacademy.training.service;

import com.fptacademy.training.domain.OutputStandard;
import com.fptacademy.training.repository.OutputStandardRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OutputStandardService {

  private final OutputStandardRepository outputStandardRepository;
  private final ModelMapper modelMapper;

  public OutputStandard save(OutputStandard outputStandard) {
    return outputStandardRepository.save(outputStandard);
  }

  public Optional<OutputStandard> update(OutputStandard outputStandard) {
    return outputStandardRepository
      .findById(outputStandard.getId())
      .map(ops -> {
        modelMapper.map(outputStandard, ops);
        return ops;
      })
      .map(outputStandardRepository::save);
  }

  @Transactional(readOnly = true)
  public List<OutputStandard> findAll() {
    return outputStandardRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<OutputStandard> findOne(Long id) {
    return outputStandardRepository.findById(id);
  }

  public void delete(Long id) {
    outputStandardRepository.deleteById(id);
  }
}
