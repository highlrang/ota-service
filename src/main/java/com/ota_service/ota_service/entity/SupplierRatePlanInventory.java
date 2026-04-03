package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER_RATEPLAN_INVENTORIES")
@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierRatePlanInventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_rate_id", nullable = false)
    private Long roomRateId;

    @Column(name = "inventory_date", nullable = false)
    private LocalDate inventoryDate;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "extra_adult", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraAdult;

    @Column(name = "extra_child", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraChild;

    @Column(name = "extra_infant", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraInfant;

    @Column(name = "promotion_type", length = 100)
    private String promotionType;

    @Column(nullable = false)
    private Integer vacancy;

    @Column(name = "stop_sale_yn", nullable = false)
    private Boolean stopSale;

    public static SupplierRatePlanInventory create(
            Long roomRateId,
            LocalDate inventoryDate,
            BigDecimal basePrice,
            BigDecimal salePrice,
            BigDecimal extraAdult,
            BigDecimal extraChild,
            BigDecimal extraInfant,
            String promotionType,
            Integer vacancy
    ) {
        return SupplierRatePlanInventory.builder()
                .roomRateId(roomRateId)
                .inventoryDate(inventoryDate)
                .basePrice(basePrice)
                .salePrice(salePrice)
                .extraAdult(extraAdult)
                .extraChild(extraChild)
                .extraInfant(extraInfant)
                .promotionType(promotionType)
                .vacancy(vacancy)
                .stopSale(vacancy == null || vacancy <= 0)
                .build();
    }

    public void update(
            BigDecimal basePrice,
            BigDecimal salePrice,
            BigDecimal extraAdult,
            BigDecimal extraChild,
            BigDecimal extraInfant,
            String promotionType,
            Integer vacancy
    ) {
        this.basePrice = basePrice;
        this.salePrice = salePrice;
        this.extraAdult = extraAdult;
        this.extraChild = extraChild;
        this.extraInfant = extraInfant;
        this.promotionType = promotionType;
        this.vacancy = vacancy;
        this.stopSale = vacancy == null || vacancy <= 0;
    }
}
