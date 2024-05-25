package com.adagency.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderElement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id")
	private Order order;
	
	@OneToMany(mappedBy = "orderElement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MediaFile> mediaFiles;
	
}
