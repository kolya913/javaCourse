package com.adagency.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Service {
	@Id
	private Long id;
	private String name;
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	@ManyToMany
	@JoinTable(
			name = "service_mediafile",
			joinColumns = @JoinColumn(name = "service_id"),
			inverseJoinColumns = @JoinColumn(name = "mediafile_id")
	)
	private Set<MediaFile> mediaFiles;
	
}
