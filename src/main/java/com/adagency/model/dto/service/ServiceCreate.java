package com.adagency.model.dto.service;


import com.adagency.model.dto.mediafile.MediaFileCreate;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreate {
	private Long categoryId;
	private String name;
	private String description;
	private List<MediaFileCreate> files;
}
