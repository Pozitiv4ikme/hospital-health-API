package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Building;

public interface BuildingRepository {

  void create(Building building);

  void update(Building building, long id);

  void deleteById(long id);

  List<Building> getAll();

  Optional<Building> getById(long id);
}
