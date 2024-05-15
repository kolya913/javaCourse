package com.adagency.model.dto.company;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateDTO {
    private Long id;
    private String name;
    private String description;
    private MediaFileCreate file;
}
