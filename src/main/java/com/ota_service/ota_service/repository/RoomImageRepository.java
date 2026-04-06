package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.RoomImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

    List<RoomImage> findAllByRoomIdInAndActiveTrueOrderByRoomIdAscSortOrderAsc(List<Long> roomIds);
}
