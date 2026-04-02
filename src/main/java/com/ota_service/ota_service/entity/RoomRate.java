package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ROOM_RATES")
@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomRate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

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

    @Column(name = "active_yn", nullable = false)
    private Boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static RoomRate create(
            Long roomId,
            String rateName,
            BigDecimal basePrice,
            String currency,
            BigDecimal salePrice,
            Boolean refundable,
            LocalDate validFrom,
            LocalDate validTo
    ) {
        return RoomRate.builder()
                .roomId(roomId)
                .rateName(rateName)
                .basePrice(basePrice)
                .currency(currency)
                .salePrice(salePrice)
                .refundable(refundable)
                .validFrom(validFrom)
                .validTo(validTo)
                .active(true)
                .build();
    }

    public void update(
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
        this.active = true;
        this.deletedAt = null;
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.active = false;
        this.deletedAt = deletedAt;
    }
}
