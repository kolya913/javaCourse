package com.adagency.model.mapper.mediaFile;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.service.ServiceEdit;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.entity.Service;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MediaFileMapper {
	
	public MediaFile createFromMediaFileCreatetoMediaFile(MediaFileCreate mediaFileCreate, String type,
	                                                      String description, String path, String alt){
		return MediaFile.builder()
				.name(mediaFileCreate.getFile().getOriginalFilename())
				.type(type)
				.path(path)
				.main(mediaFileCreate.isMain())
				.size(mediaFileCreate.getFile().getSize())
				.createdAt(new Date())
				.updatedAt(new Date())
				.description(description)
				.alt(alt)
				.build();
	}

	public MediaFileView createMediaFileViewFromMediaFile(MediaFile mediaFile){
		return MediaFileView.builder()
				.fileId(mediaFile.getId())
				.fileAlt(mediaFile.getAlt())
				.isMain(mediaFile.isMain())
				.fileDescription(mediaFile.getDescription())
				.build();
	}
	
	
	public void updateMediaFileFromMediaFileView(MediaFile mediaFile, MediaFileView mediaFileView){
		mediaFile.setName(mediaFileView.getFile().getOriginalFilename());
		mediaFile.setDescription(mediaFileView.getFileDescription());
		mediaFile.setAlt(mediaFileView.getFileAlt());
		mediaFile.setMain(mediaFileView.isMain());
		mediaFile.setUpdatedAt(new Date());
	}


	
}
