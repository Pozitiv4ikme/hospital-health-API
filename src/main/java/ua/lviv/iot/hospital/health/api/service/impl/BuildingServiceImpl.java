package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.repository.BuildingRepository;
import ua.lviv.iot.hospital.health.api.service.BuildingService;
import ua.lviv.iot.hospital.health.api.service.RoomService;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {
  private final BuildingRepository buildingRepository;
  private final RoomService roomService;

  @Override
  public Optional<BuildingDto> getById(long id) {
    return buildingRepository.getById(id)
        .map(building -> buildBuildingDto(building, roomService.getAllByBuildingId(id)));
  }

  @Override
  public List<BuildingDto> getAll() {
    return buildingRepository.getAll().stream()
        .map(building -> buildBuildingDto(building, roomService.getAllByBuildingId(building.getId())))
        .toList();
  }

  @Override
  public List<BuildingDto> getAllByHospitalId(long hospitalId) {
    return getAll().stream()
        .filter(buildingDto -> buildingDto.hospitalId() == hospitalId)
        .toList();
  }

  @Override
  public void create(Building building) {
    buildingRepository.create(building);
  }

  @Override
  public void update(Building building, long id) {
    buildingRepository.update(building, id);
  }

  @Override
  public void deleteById(long id) {
    buildingRepository.deleteById(id);
  }

  private BuildingDto buildBuildingDto(Building building, List<RoomDto> rooms) {
    return BuildingDto.builder()
        .id(building.getId())
        .name(building.getName())
        .address(building.getAddress())
        .hospitalId(building.getHospitalId())
        .rooms(rooms)
        .build();
  }
}
