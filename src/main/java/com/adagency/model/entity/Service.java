package com.adagency.model.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Service {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Length(max = 300)
	private String name;
	@Length(max = 400)
	private String shortDescription;
	@Length(max = 5000)
	private String description;
	
	@Column(name = "deleteFlag")
	private boolean deleteFlag = false;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;
	

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "service_mediafile",
			joinColumns = @JoinColumn(name = "service_id"),
			inverseJoinColumns = @JoinColumn(name = "mediafile_id")
	)
	private List<MediaFile> mediaFiles;
	
	
	@OneToMany(mappedBy = "service",fetch = FetchType.LAZY)
	private List<ServicePricing> servicePricings;
	
}
