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
@Table(name = "SELLER_ACCOMMODATIONS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerAccommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "accommodation_id", nullable = false, unique = true)
    private Long accommodationId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    public static SellerAccommodation of(Long sellerId, Long accommodationId, String code) {
        SellerAccommodation sellerAccommodation = new SellerAccommodation();
        sellerAccommodation.sellerId = sellerId;
        sellerAccommodation.accommodationId = accommodationId;
        sellerAccommodation.code = code;
        return sellerAccommodation;
    }
}
