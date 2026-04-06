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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER_RATEPLANS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierRatePlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupplierSourceType source;

    @Column(name = "supplier_rateplan_id", nullable = false, length = 100)
    private String supplierRateplanId;

    @Column(name = "room_rate_id", nullable = false, unique = true)
    private Long roomRateId;

    @Column(name = "supplier_room_id", nullable = false)
    private Long supplierRoomId;

    @Column(name = "rate_name", nullable = false, length = 255)
    private String rateName;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "refundable_yn", nullable = false)
    private Boolean refundable;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;

    @Column(length = 30)
    private String status;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    public static SupplierRatePlan of(
            SupplierSourceType source,
            String supplierRateplanId,
            Long roomRateId,
            Long supplierRoomId
    ) {
        SupplierRatePlan supplierRatePlan = new SupplierRatePlan();
        supplierRatePlan.source = source;
        supplierRatePlan.supplierRateplanId = supplierRateplanId;
        supplierRatePlan.roomRateId = roomRateId;
        supplierRatePlan.supplierRoomId = supplierRoomId;
        supplierRatePlan.lastSyncedAt = LocalDateTime.now();
        return supplierRatePlan;
    }

    public void updateContent(
            String rateName,
            BigDecimal basePrice,
            String currency,
            BigDecimal salePrice,
            Boolean refundable,
            LocalDate validFrom,
            LocalDate validTo
    ) {
        this.rateName = rateName;
        this.basePrice = basePrice;
        this.currency = currency;
        this.salePrice = salePrice;
        this.refundable = refundable;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public void markSynced(String status) {
        this.status = status;
        this.lastSyncedAt = LocalDateTime.now();
    }
}
