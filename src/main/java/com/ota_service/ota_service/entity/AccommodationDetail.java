package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACCOMMODATION_DETAILS")
@Getter
@Setter
public class AccommodationDetail extends BaseTimeEntity {

    @Id
    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "extra_info", length = 1000)
    private String extraInfo;
}
