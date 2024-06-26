package com.adagency.model.entity;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "category")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name="Description")
    @Length(max = 1500)
    private String description;

    @Column(name = "deleteFlag")
    private Boolean deleteFlag;

    @Column(name = "dateCreateAt")
    private Date dateCreateAt;

    @Column(name = "dateLastUpdate")
    private Date dateLastUpdate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mediaFile_id")
    private MediaFile picture;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Service> services;
    
    
}
