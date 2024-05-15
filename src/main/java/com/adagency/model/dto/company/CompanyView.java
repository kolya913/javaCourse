package com.adagency.model.dto.company;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class CompanyView {
	private Long id;
	private String name;
	private String filePath;
	private String fileAlt;
	private String fileDescription;
	private Long fileId;
	private MultipartFile file;
}
