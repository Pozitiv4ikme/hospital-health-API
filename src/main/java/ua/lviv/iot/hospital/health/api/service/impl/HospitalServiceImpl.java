package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.dto.HospitalDto;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.repository.HospitalRepository;
import ua.lviv.iot.hospital.health.api.service.BuildingService;
import ua.lviv.iot.hospital.health.api.service.HospitalService;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
  private final HospitalRepository hospitalRepository;
  private final BuildingService buildingService;

  @Override
  public List<HospitalDto> getAll() {
    return hospitalRepository.getAll().stream()
        .map(hospital -> buildHospitalDto(hospital, buildingService.getAllByHospitalId(hospital.getId())))
        .toList();
  }

  @Override
  public Optional<HospitalDto> getById(long id) {
    return hospitalRepository.getById(id)
        .map(hospital -> buildHospitalDto(hospital, buildingService.getAllByHospitalId(id)));
  }

  @Override
  public void create(Hospital hospital) {
    hospitalRepository.create(hospital);
  }

  @Override
  public void update(Hospital hospital, long id) {
    hospitalRepository.update(hospital, id);
  }

  @Override
  public void deleteById(long id) {
    hospitalRepository.deleteById(id);
  }

  private HospitalDto buildHospitalDto(Hospital hospital, List<BuildingDto> buildings) {
    return HospitalDto.builder()
        .id(hospital.getId())
        .name(hospital.getName())
        .buildings(buildings)
        .build();
  }
}
