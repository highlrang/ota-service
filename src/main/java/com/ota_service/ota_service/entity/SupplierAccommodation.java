package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.SupplierSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER_ACCOMMODATIONS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierAccommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupplierSourceType source;

    @Column(name = "supplier_property_id", nullable = false, length = 100)
    private String supplierPropertyId;

    @Column(name = "accommodation_id", nullable = false, unique = true)
    private Long accommodationId;

    @Column(length = 30)
    private String status;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    public static SupplierAccommodation of(
            SupplierSourceType source,
            String supplierPropertyId,
            Long accommodationId
    ) {
        SupplierAccommodation supplierAccommodation = new SupplierAccommodation();
        supplierAccommodation.source = source;
        supplierAccommodation.supplierPropertyId = supplierPropertyId;
        supplierAccommodation.accommodationId = accommodationId;
        supplierAccommodation.lastSyncedAt = LocalDateTime.now();
        return supplierAccommodation;
    }

    public void markSynced(String status) {
        this.status = status;
        this.lastSyncedAt = LocalDateTime.now();
    }
}
