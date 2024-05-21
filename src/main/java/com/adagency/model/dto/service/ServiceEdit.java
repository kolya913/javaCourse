package com.adagency.model.dto.service;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.status.StatusView;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEdit {
	private Long id;
	@NotEmpty
	private String name;
	@NotEmpty
	private String shortDescription;
	@NotEmpty
	private String description;
	private boolean deleteFlag;

	private StatusView status;

	private Long statusId;

	private List<MediaFileView> mediaFiles;

	private List<MediaFileCreate> mediaFileCreates;

	@Nullable
	private Long categoryId;
}
