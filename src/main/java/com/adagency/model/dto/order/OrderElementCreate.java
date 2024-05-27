package com.adagency.model.dto.order;

import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderElementCreate {
	private Long orderElementId;
	private ServicePricingView servicePricingView;
	private Integer count;
	private String text;
	private List<MediaFileCreate> file = new ArrayList<>();
}
