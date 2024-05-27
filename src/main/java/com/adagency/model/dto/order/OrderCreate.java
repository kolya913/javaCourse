package com.adagency.model.dto.order;

import com.adagency.model.dto.category.ClientSimpleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreate {
	private Long clientId;
	private List<ClientSimpleCategory> clientSimpleCategoryList;
	private List<Long> selectedServicePricingIds;
}
