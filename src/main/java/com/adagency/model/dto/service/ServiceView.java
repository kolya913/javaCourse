package com.adagency.model.dto.service;

import com.adagency.model.dto.mediafile.MediaFileView;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceView {
	private Long id;
	private String name;
	private String description;
	private List<MediaFileView> media;
	private Long categoryId;
}
