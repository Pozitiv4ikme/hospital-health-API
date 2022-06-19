package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.entity.Building;

public interface BuildingService {

  Optional<BuildingDto> getById(long id);

  List<BuildingDto> getAll();

  List<BuildingDto> getAllByHospitalId(long hospitalId);

  void create(Building building);

  void update(Building building, long id);

  void deleteById(long id);
}
