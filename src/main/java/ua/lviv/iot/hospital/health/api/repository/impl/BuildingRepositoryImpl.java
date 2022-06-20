package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.building.BuildingRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.repository.BuildingRepository;
import ua.lviv.iot.hospital.health.api.repository.HospitalRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.BuildingStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BuildingRepositoryImpl implements BuildingRepository {

  private final BuildingStorage buildingStorage;
  private final HospitalRepository hospitalRepository;

  @Override
  public void create(Building building) {
    if (getById(building.getId()).isPresent()) {
      String message = "Cannot create building, because building with id " + building.getId() + " already exists";
      log.error(message);
      throw new BuildingRepositoryException(message);
    }

    doesHospitalExist(building);
    buildingStorage.create(building);
  }

  @Override
  public void update(long id, Building building) {
    if (getById(id).isEmpty()) {
      String message = "Cannot update building, because building with id " + id + " does not exist";
      log.error(message);
      throw new BuildingRepositoryException(message);
    }

    doesHospitalExist(building);
    buildingStorage.update(building, id);
  }

  @Override
  public void deleteById(long id) {
    buildingStorage.deleteById(id);
  }

  @Override
  public List<Building> getAll() {
    return buildingStorage.getAll();
  }

  @Override
  public Optional<Building> getById(long id) {
    return buildingStorage.getById(id);
  }

  private void doesHospitalExist(Building building) {
    if (hospitalRepository.getById(building.getHospitalId()).isEmpty()) {
      String message = "Hospital with id " + building.getHospitalId() + " does not exist";
      log.error(message);
      throw new BuildingRepositoryException(message);
    }
  }
}
