package com.adagency.model.dto.service;


import com.adagency.model.dto.mediafile.MediaFileCreate;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreate {
	@NotNull
	@NumberFormat
	private Long categoryId;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	private List<MediaFileCreate> files;
	public ServiceCreate(Long categoryId){
		this.categoryId = categoryId;
	}
}
