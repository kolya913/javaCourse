package com.adagency.model.dto.service;

import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.status.StatusView;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEdit {
	private Long id;
	private String name;
	private String shortDescription;
	private String description;
	private boolean deleteFlag;
	private StatusView status;
	private Long statusId;
	private List<MediaFileView> mediaFiles;
	@Nullable
	private Long categoryId;
}
