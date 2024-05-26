package com.adagency.model.dto.category;

import com.adagency.model.dto.service.ServiceSimpleView;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientSimpleCategory {
	private Long id;
	private String name;
	private List<ServiceSimpleView> clientSimpleCategoryList;
}
