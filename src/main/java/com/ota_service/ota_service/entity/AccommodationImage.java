package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACCOMMODATION_IMAGES")
@Getter
@Setter
public class AccommodationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
