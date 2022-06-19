package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.dto.HospitalDto;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;

public interface HospitalService {
  Optional<HospitalDto> getById(long id);
  List<HospitalDto> getAll();
  void create(Hospital hospital);
  void update(Hospital hospital, long id);
  void deleteById(long id);
}
