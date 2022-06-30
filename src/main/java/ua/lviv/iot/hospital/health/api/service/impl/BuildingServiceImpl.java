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
public final class BuildingServiceImpl implements BuildingService {

  private final BuildingRepository buildingRepository;
  private final RoomService roomService;

  @Override
  public Optional<BuildingDto> getById(final long id) {
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
  public List<BuildingDto> getAllByHospitalId(final long hospitalId) {
    return getAll().stream()
        .filter(buildingDto -> buildingDto.hospitalId() == hospitalId)
        .toList();
  }

  @Override
  public void create(final Building building) {
    buildingRepository.create(building);
  }

  @Override
  public void update(final long id, final Building building) {
    buildingRepository.update(id, building);
  }

  @Override
  public void deleteById(final long id) {
    buildingRepository.deleteById(id);
  }

  private BuildingDto buildBuildingDto(final Building building, final List<RoomDto> rooms) {
    return BuildingDto.builder()
        .id(building.getId())
        .name(building.getName())
        .address(building.getAddress())
        .hospitalId(building.getHospitalId())
        .rooms(rooms)
        .build();
  }
}
