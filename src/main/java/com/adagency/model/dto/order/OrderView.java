package com.adagency.model.dto.order;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderView {
	private Long id;
	private Long workerId;
	private Long clientId;
	private Long statusId;
}
