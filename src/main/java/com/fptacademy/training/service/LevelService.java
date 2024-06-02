package com.fptacademy.training.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fptacademy.training.domain.Level;
import com.fptacademy.training.exception.ResourceNotFoundException;
import com.fptacademy.training.repository.LevelRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class LevelService {
    private final LevelRepository levelRepository;
    private final ModelMapper modelMapper;

    public Level getLevelByName(String name) {
        return levelRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Level " + name + " not found"));
    }

    public Level save(Level level) {
        return levelRepository.save(level);
    }

    public Optional<Level> update(Level level) {
        return levelRepository
                .findById(level.getId())
                .map(ops -> {
                    modelMapper.map(level, ops);
                    return ops;
                })
                .map(levelRepository::save);
    }

    @Transactional(readOnly = true)
    public List<Level> findAll() {
        return levelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Level> findOne(Long id) {
        return levelRepository.findById(id);
    }

    public void delete(Long id) {
        levelRepository.deleteById(id);
    }

    public boolean checkLevelIsExist(String name){
        Optional<Level> level =  levelRepository.findByName(name);
        return level.isEmpty();
    }
}
