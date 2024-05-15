package com.adagency.model.mapper.mediaFile;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.entity.MediaFile;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MediaFileMapper {
	
	public MediaFile createFromMediaFileCreatetoMediaFile(MediaFileCreate mediaFileCreate, String type,
	                                                      String description, String path, String alt){
		return MediaFile.builder()
				.name(mediaFileCreate.getFile().getName())
				.type(type)
				.path(path)
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
				.fileDescription(mediaFile.getDescription())
				.build();
	}

	
}
