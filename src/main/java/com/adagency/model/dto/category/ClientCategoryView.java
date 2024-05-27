package com.adagency.model.dto.category;

import com.adagency.model.dto.mediafile.MediaFileView;

import com.adagency.model.dto.service.ServiceSimpleView;
import lombok.*;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientCategoryView {
	@Null
	private Long id;
	private String name;
	private String description;
	private MediaFileView file;
	private List<ServiceSimpleView> services;
}
