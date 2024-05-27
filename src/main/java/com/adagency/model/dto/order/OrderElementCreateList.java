package com.adagency.model.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderElementCreateList {
	private Long orderId;
	private List<OrderElementCreate> orderElementCreateList;
}
