package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EXTRANET_ACCOMMODATIONS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtranetAccommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "extranet_id", nullable = false)
    private Long extranetId;

    @Column(name = "accommodation_id", nullable = false, unique = true)
    private Long accommodationId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    public static ExtranetAccommodation of(Long extranetId, Long accommodationId, String code) {
        ExtranetAccommodation extranetAccommodation = new ExtranetAccommodation();
        extranetAccommodation.extranetId = extranetId;
        extranetAccommodation.accommodationId = accommodationId;
        extranetAccommodation.code = code;
        return extranetAccommodation;
    }
}
