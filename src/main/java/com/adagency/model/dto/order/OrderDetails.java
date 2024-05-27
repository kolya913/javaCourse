package com.adagency.model.dto.order;

import com.adagency.model.dto.orderelement.OrderElementView;
import com.adagency.model.dto.status.StatusView;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetails {
	private Long id;
	private Long workerId;
	private Long clientId;
	private Long statusId;
	private StatusView statusView;
	private List<OrderElementView> orderElementViews;
}
