package com.adagency.model.entity;
//todo dto create view error mapper service repo pagination
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Builder
@Table(name = "mediafile")
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "path")
    @Nullable
    private String path;
    
    @Column(name = "size")
    private Long size;
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "updatedAt")
    private Date updatedAt;
    
    @Column(name = "description")
    @Length(max = 2500)
    private String description;
    
    @Column(name = "alt")
    @Length(max=100)
    private String alt;

    @Column(name = "main")
    private boolean main = false;
    

    @OneToOne(mappedBy = "picture", fetch = FetchType.LAZY)
    private Category category;
    
    @OneToOne(mappedBy = "logo", fetch = FetchType.LAZY)
    private Company company;

    @ManyToMany(mappedBy = "mediaFiles", fetch = FetchType.LAZY)
    private Set<Service> services;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_element_id")
    private OrderElement orderElement;
    
}
