package com.adagency.model.mapper.status;

import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {
	public StatusView fromStatusToStatusView(Status status){
		return StatusView.builder()
				.id(status.getId())
				.name(status.getName())
				.build();
	}
}
