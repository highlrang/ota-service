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
@Table(name = "SUPPLIER_ROOMS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupplierSourceType source;

    @Column(name = "supplier_roomtype_id", nullable = false, length = 100)
    private String supplierRoomtypeId;

    @Column(name = "room_id", nullable = false, unique = true)
    private Long roomId;

    @Column(name = "supplier_accommodation_id", nullable = false)
    private Long supplierAccommodationId;

    @Column(length = 30)
    private String status;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    public static SupplierRoom of(
            SupplierSourceType source,
            String supplierRoomtypeId,
            Long roomId,
            Long supplierAccommodationId
    ) {
        SupplierRoom supplierRoom = new SupplierRoom();
        supplierRoom.source = source;
        supplierRoom.supplierRoomtypeId = supplierRoomtypeId;
        supplierRoom.roomId = roomId;
        supplierRoom.supplierAccommodationId = supplierAccommodationId;
        supplierRoom.lastSyncedAt = LocalDateTime.now();
        return supplierRoom;
    }

    public void markSynced(String status) {
        this.status = status;
        this.lastSyncedAt = LocalDateTime.now();
    }
}
