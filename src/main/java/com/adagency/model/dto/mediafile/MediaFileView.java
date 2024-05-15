package com.adagency.model.dto.mediafile;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileView {
	private String filePath;
	private String fileAlt;
	private String fileDescription;
	private Long fileId;
	private MultipartFile file;
}
