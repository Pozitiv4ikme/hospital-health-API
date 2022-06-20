package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.RepositoryNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.building.BuildingRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.repository.BuildingRepository;
import ua.lviv.iot.hospital.health.api.repository.HospitalRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.BuildingStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public final class BuildingRepositoryImpl implements BuildingRepository {

  private final BuildingStorage buildingStorage;
  private final HospitalRepository hospitalRepository;

  @Override
  public void create(final Building building) {
    if (getById(building.getId()).isPresent()) {
      final String message = "Cannot create building, because building with id " + building.getId() + " already exists";
      log.error(message);
      throw new BuildingRepositoryException(message);
    }

    doesHospitalExist(building);
    buildingStorage.create(building);
  }

  @Override
  public void update(final long id, final Building building) {
    if (getById(id).isEmpty()) {
      final String message = "Cannot update building, because building with id " + id + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }

    doesHospitalExist(building);
    buildingStorage.update(id, building);
  }

  @Override
  public void deleteById(final long id) {
    buildingStorage.deleteById(id);
  }

  @Override
  public List<Building> getAll() {
    return buildingStorage.getAll();
  }

  @Override
  public Optional<Building> getById(final long id) {
    return buildingStorage.getById(id);
  }

  private void doesHospitalExist(final Building building) {
    if (hospitalRepository.getById(building.getHospitalId()).isEmpty()) {
      final String message = "Hospital with id " + building.getHospitalId() + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }
  }
}
