package com.adagency.model.dto.mediafile;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileCreate {
    private String description;
    private String alt;
    private MultipartFile file;
}
