package com.adagency.model.dto.category;

import javax.validation.constraints.Null;

import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.Date;
import java.util.List;

import com.adagency.model.dto.mediafile.MediaFileView;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryView {
    @Null
    private Long id;
    private String name;
    private Long statusId;
    private StatusView status;
    private String description;
    private Boolean deleteFlag;
    private Date dateCreateAt;
    private Date dateLastUpdate;
    private MediaFileView file;
    private List<Service> services; //todo dto + add to getCategoryView
}
