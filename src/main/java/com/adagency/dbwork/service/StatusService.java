package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.StatusRepository;
import com.adagency.model.dto.category.CategoryView;
import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatusService {

	private final StatusRepository statusRepository;
	
	@Autowired
	public StatusService(StatusRepository statusRepository){
		this.statusRepository = statusRepository;
	}
	
	public List<StatusView> getAll(){
		return statusRepository.findAll().stream()
				.map(status -> StatusView.builder().id(status.getId()).name(status.getName()).build())
				.collect(Collectors.toList());
	}
	
	@Transactional
	public void create(Status status){
		statusRepository.save(status);
	}
	
	public Optional<Status> findByName(String name){
		return statusRepository.findByName(name);
	}
	
	public Optional<Status> findById(Long id){
		return statusRepository.findById(id);
	}
	
}
