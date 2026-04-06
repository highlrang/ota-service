package com.ota_service.ota_service.repository;

import com.ota_service.ota_service.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByAccommodationIdOrderByIdAsc(Long accommodationId);

    List<Room> findAllByAccommodationIdInAndActiveTrueOrderByAccommodationIdAscIdAsc(List<Long> accommodationIds);

    Optional<Room> findByRoomCode(String roomCode);
}
