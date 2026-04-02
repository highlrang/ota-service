package com.ota_service.ota_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ROOM_INVENTORIES")
@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomInventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "inventory_date", nullable = false)
    private LocalDate inventoryDate;

    @Column(name = "total_stock", nullable = false)
    private Integer totalStock;

    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;

    @Column(name = "stop_sale_yn", nullable = false)
    private Boolean stopSale;

    @Column(name = "active_yn", nullable = false)
    private Boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static RoomInventory create(
            Long roomId,
            LocalDate inventoryDate,
            Integer totalStock,
            Integer reservedStock,
            Integer availableStock,
            Boolean stopSale
    ) {
        return RoomInventory.builder()
                .roomId(roomId)
                .inventoryDate(inventoryDate)
                .totalStock(totalStock)
                .reservedStock(reservedStock)
                .availableStock(availableStock)
                .stopSale(stopSale)
                .active(true)
                .build();
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.active = false;
        this.deletedAt = deletedAt;
    }
}
