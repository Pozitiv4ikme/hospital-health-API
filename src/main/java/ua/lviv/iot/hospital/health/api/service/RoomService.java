package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Room;

public interface RoomService {

  Optional<RoomDto> getById(long id);

  List<RoomDto> getAll();

  List<RoomDto> getAllByBuildingId(long buildingId);

  void create(Room room);

  void update(Room room, long id);

  void deleteById(long id);
}
