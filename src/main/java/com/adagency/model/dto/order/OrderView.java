package com.adagency.model.dto.order;

import com.adagency.model.dto.orderelement.OrderElementView;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.status.StatusView;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderView {
	private Long id;
	private UserProfileForm worker;
	private UserProfileForm client;
	private StatusView statusView;
	private Long statusId;
	private Long workerId;
	private Long clientId;
	private Boolean payed;
	private List<OrderElementView> orderElementViewList;
}
