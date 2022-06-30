package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.EntityNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.RoomRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.repository.BuildingRepository;
import ua.lviv.iot.hospital.health.api.repository.RoomRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.RoomStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public final class RoomRepositoryImpl implements RoomRepository {

  private final RoomStorage roomStorage;
  private final BuildingRepository buildingRepository;

  @Override
  public void create(final Room room) {
    if (getById(room.getId()).isPresent()) {
      final String message = "Cannot create room, because room with id " + room.getId() + " already exists";
      log.error(message);
      throw new RoomRepositoryException(message);
    }

    doesBuildingExist(room);
    roomStorage.create(room);
  }

  @Override
  public void update(final long id, final Room room) {
    if (getById(id).isEmpty()) {
      final String message = "Cannot update room, because room with id " + id + " does not exist";
      log.error(message);
      throw new EntityNotFoundException(message);
    }

    doesBuildingExist(room);
    roomStorage.update(id, room);
  }

  @Override
  public void deleteById(final long id) {
    roomStorage.deleteById(id);
  }

  @Override
  public List<Room> getAll() {
    return roomStorage.getAll();
  }

  @Override
  public Optional<Room> getById(final long id) {
    return roomStorage.getById(id);
  }

  private void doesBuildingExist(final Room room) {
    if (buildingRepository.getById(room.getBuildingId()).isEmpty()) {
      final String message = "Building with id " + room.getBuildingId() + " does not exist";
      log.error(message);
      throw new EntityNotFoundException(message);
    }
  }
}
