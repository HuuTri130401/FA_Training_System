package com.fptacademy.training.service;

import com.fptacademy.training.domain.Delivery;
import com.fptacademy.training.repository.DeliveryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final ModelMapper modelMapper;

  public Delivery save(Delivery delivery) {
    return deliveryRepository.save(delivery);
  }

  public Optional<Delivery> update(Delivery delivery) {
    return deliveryRepository
      .findById(delivery.getId())
      .map(ops -> {
        modelMapper.map(delivery, ops);
        return ops;
      })
      .map(deliveryRepository::save);
  }

  @Transactional(readOnly = true)
  public List<Delivery> findAll() {
    return deliveryRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Delivery> findOne(Long id) {
    return deliveryRepository.findById(id);
  }

  public void delete(Long id) {
    deliveryRepository.deleteById(id);
  }
}
