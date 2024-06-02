package com.adagency.model.dto.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchFormForOrder {
	private Long orderId;
	private Long workerId;
	private Long clientId;
}
