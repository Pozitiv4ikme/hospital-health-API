package ua.lviv.iot.hospital.health.api.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.service.BuildingService;

@RestController
@RequestMapping("buildings")
@RequiredArgsConstructor
public class BuildingController {

  private final BuildingService buildingService;

  @PostMapping
  public void create(@RequestBody Building building) {
    buildingService.create(building);
  }

  @PutMapping("{id}")
  public void update(@PathVariable("id") long id, @RequestBody Building building) {
    buildingService.update(building, id);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable("id") long id) {
    buildingService.deleteById(id);
  }

  @GetMapping("{id}")
  public Optional<BuildingDto> getById(@PathVariable("id") long id) {
    return buildingService.getById(id);
  }

  @GetMapping
  public List<BuildingDto> getAll() {
    return buildingService.getAll();
  }


  @GetMapping("{id}/rooms")
  public List<RoomDto> getAllRoomsByBuildingId(@PathVariable("id") long id) {
    return getById(id).map(BuildingDto::rooms).orElse(List.of());
  }

  @GetMapping("{id}/rooms/{roomId}/patients")
  public List<PatientDto> getAllPatientsByBuildingIdAndRoomId(@PathVariable("id") long id,
      @PathVariable("roomId") long roomId) {
    return getAllRoomsByBuildingId(id).stream()
        .filter(roomDto -> roomDto.id() == roomId)
        .flatMap(roomDto -> roomDto.patients().stream())
        .toList();
  }
}
