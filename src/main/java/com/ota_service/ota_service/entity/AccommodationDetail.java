package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ACCOMMODATION_DETAILS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccommodationDetail extends BaseTimeEntity {

    @Id
    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "extra_info", length = 1000)
    private String extraInfo;

    public static AccommodationDetail of(Long accommodationId, String description, String extraInfo) {
        AccommodationDetail detail = new AccommodationDetail();
        detail.accommodationId = accommodationId;
        detail.description = description;
        detail.extraInfo = extraInfo;
        return detail;
    }

    public void update(String description, String extraInfo) {
        this.description = description;
        this.extraInfo = extraInfo;
    }
}
