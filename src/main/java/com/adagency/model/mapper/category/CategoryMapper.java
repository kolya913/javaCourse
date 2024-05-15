package com.adagency.model.mapper.category;

import com.adagency.model.dto.category.CategoryCreateDTO;
import com.adagency.model.dto.category.CategoryView;
import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
	public Category createCategoryDTOTOCategory(CategoryCreateDTO categoryCreateDTO){
		return Category.builder()
				.name(categoryCreateDTO.getName())
				.description(categoryCreateDTO.getDescription())
				.build();
	}

	public CategoryView fromCategoryToCategoryView(Category category){
		return  CategoryView.builder()
				.id(category.getId())
				.name(category.getName())
				.status(StatusView.builder().name(category.getStatus().getName()).id(category.getStatus().getId()).build())
				.description(category.getDescription())
				.deleteFlag(category.getDeleteFlag())
				.dateCreateAt(category.getDateCreateAt())
				.dateLastUpdate(category.getDateLastUpdate())
				.build();
	}
	
	public Category updateCategory(Category category, CategoryView categoryView){
		category.setName(categoryView.getName());
		category.setDescription(categoryView.getDescription());
		category.setDeleteFlag(categoryView.getDeleteFlag());
		return category;
	}

}
