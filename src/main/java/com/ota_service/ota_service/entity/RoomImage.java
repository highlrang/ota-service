package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.ImageType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ROOM_IMAGES")
@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 50)
    private ImageType imageType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "active_yn", nullable = false)
    private Boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static RoomImage create(Long roomId, String imageUrl, ImageType imageType, Integer sortOrder) {
        return RoomImage.builder()
                .roomId(roomId)
                .imageUrl(imageUrl)
                .imageType(imageType)
                .sortOrder(sortOrder)
                .active(true)
                .build();
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.active = false;
        this.deletedAt = deletedAt;
    }
}
