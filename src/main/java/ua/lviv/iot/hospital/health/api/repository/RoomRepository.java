package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Room;

public interface RoomRepository {

  void create(Room room);

  void update(Room room, long id);

  void deleteById(long id);

  List<Room> getAll();

  Optional<Room> getById(long id);
}
