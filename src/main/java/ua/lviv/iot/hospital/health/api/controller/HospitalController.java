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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.dto.HospitalDto;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.service.BuildingService;
import ua.lviv.iot.hospital.health.api.service.HospitalService;
import ua.lviv.iot.hospital.health.api.service.PatientService;

@RestController
@RequestMapping("hospitals")
@RequiredArgsConstructor
public final class HospitalController {

  private final HospitalService hospitalService;
  private final BuildingService buildingService;
  private final PatientService patientService;

  @PostMapping
  public void create(@RequestBody final Hospital hospital) {
    hospitalService.create(hospital);
  }

  @PutMapping("{id}")
  public void update(@RequestBody final Hospital hospital, @PathVariable final long id) {
    hospitalService.update(id, hospital);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable final long id) {
    hospitalService.deleteById(id);
  }

  @GetMapping
  public List<HospitalDto> getAll() {
    return hospitalService.getAll();
  }

  @GetMapping("{id}")
  public Optional<HospitalDto> getById(@PathVariable("id") final long id) {
    return hospitalService.getById(id);
  }

  @GetMapping("{id}/buildings")
  public List<BuildingDto> getAllBuildingsByHospitalId(@PathVariable("id") final long hospitalId) {
    return buildingService.getAllByHospitalId(hospitalId);
  }

  @GetMapping("{id}/buildings/{buildingId}/rooms")
  public List<RoomDto> getAllRoomsByHospitalIdAndBuildingId(@PathVariable("id") final long hospitalId,
      @PathVariable("buildingId") long buildingId) {
    return getAllBuildingsByHospitalId(hospitalId).stream()
        .filter(buildingDto -> buildingDto.id() == buildingId)
        .flatMap(buildingDto -> buildingDto.rooms().stream())
        .toList();
  }

  @GetMapping("{id}/buildings/{buildingId}/rooms/{roomId}/patients")
  public List<PatientDto> getAllPatientsByHospitalIdAndBuildingIdAndRoomId(@PathVariable("id") final long hospitalId,
      @PathVariable("buildingId") long buildingId, @PathVariable("roomId") long roomId) {
    return getAllRoomsByHospitalIdAndBuildingId(hospitalId, buildingId).stream()
        .filter(roomDto -> roomDto.id() == roomId)
        .flatMap(roomDto -> roomDto.patients().stream())
        .toList();
  }

  @GetMapping("{id}/rooms")
  public List<RoomDto> getAllRoomsByHospitalId(@PathVariable("id") final long hospitalId) {
    return hospitalService.getById(hospitalId)
        .map(hospitalDto -> hospitalDto.buildings()
            .stream().flatMap(buildingDto -> buildingDto.rooms().stream()).toList())
        .orElse(List.of());
  }

  @GetMapping("{id}/patients")
  public List<PatientDto> getAllPatientsByHealthStatus(@PathVariable("id") final long hospitalId,
      @RequestParam("status") final Optional<HealthStatus> statusOptional) {
    return hospitalService.getById(hospitalId)
        .map(hospitalDto -> hospitalDto.buildings()
            .stream()
            .flatMap(buildingDto -> buildingDto.rooms().stream())
            .flatMap(roomDto -> roomDto.patients().stream())
            .filter(patientDto -> hasStatus(statusOptional, patientDto))
            .toList())
        .orElse(List.of());

  }

  private Boolean hasStatus(final Optional<HealthStatus> statusOptional, final PatientDto patientDto) {
    return statusOptional.map(status -> status == patientService.getStatusById(patientDto.id())).orElse(true);
  }
}
