package com.adagency.model.dto.orderelement;

import com.adagency.model.dto.mediafile.MediaFileView;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderElementView {
	private Long id;
	private Integer count;
	private String text;
	private String serviceName;
	private Long serviceId;
	private List<MediaFileView> mediaFileViews;
}
