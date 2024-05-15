package com.adagency.model.dto.category;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDTO {
    private String name;
    private String description;
    private MediaFileCreate file;
}
