package com.ota_service.ota_service.entity;

import com.ota_service.ota_service.enums.BedType;
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
@Table(name = "ROOMS")
@Getter
@Setter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "room_code", nullable = false, unique = true, length = 100)
    private String roomCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "standard_occupancy", nullable = false)
    private Integer standardOccupancy;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", length = 100)
    private BedType bedType;

    @Column(name = "extra_info", length = 500)
    private String extraInfo;

    @Column(name = "min_stay_nights", nullable = false)
    private Integer minStayNights;

    @Column(name = "max_stay_nights", nullable = false)
    private Integer maxStayNights;

    @Column(name = "active_yn", nullable = false)
    private Boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Room create(
            Long accommodationId,
            String roomCode,
            String name,
            String description,
            Integer standardOccupancy,
            Integer maxOccupancy,
            BedType bedType,
            String extraInfo,
            Integer minStayNights,
            Integer maxStayNights
    ) {
        return Room.builder()
                .accommodationId(accommodationId)
                .roomCode(roomCode)
                .name(name)
                .description(description)
                .standardOccupancy(standardOccupancy)
                .maxOccupancy(maxOccupancy)
                .bedType(bedType)
                .extraInfo(extraInfo)
                .minStayNights(minStayNights)
                .maxStayNights(maxStayNights)
                .active(true)
                .deletedAt(null)
                .build();
    }

    public void changeRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void update(
            String name,
            String description,
            Integer standardOccupancy,
            Integer maxOccupancy,
            BedType bedType,
            String extraInfo,
            Integer minStayNights,
            Integer maxStayNights
    ) {
        this.name = name;
        this.description = description;
        this.standardOccupancy = standardOccupancy;
        this.maxOccupancy = maxOccupancy;
        this.bedType = bedType;
        this.extraInfo = extraInfo;
        this.minStayNights = minStayNights;
        this.maxStayNights = maxStayNights;
        this.active = true;
        this.deletedAt = null;
    }

    public void activate() {
        this.active = true;
        this.deletedAt = null;
    }

    public void deactivate(LocalDateTime deletedAt) {
        this.active = false;
        this.deletedAt = deletedAt;
    }
}
