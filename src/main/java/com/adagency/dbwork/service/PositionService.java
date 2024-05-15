package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.PositionRepository;
import com.adagency.model.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {
    private final PositionRepository positionRepository;

    @Autowired
    private PositionService(PositionRepository positionRepository){
        this.positionRepository = positionRepository;
    }

    public void save(Position position){
        positionRepository.save(position);
    }

    public Optional<Position> findByName(String name){
        return positionRepository.findByName(name);
    }

    public List<Position> getAll(){
        return positionRepository.findAll();
    }
    
    public Optional<Position> findById(Long id) {
        return positionRepository.findById(id);
    }
}
