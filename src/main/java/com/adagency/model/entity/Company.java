package com.adagency.model.entity;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    @Nullable
    private MediaFile logo;
    
}
