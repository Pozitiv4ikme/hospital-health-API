package ua.lviv.iot.hospital.health.api.service;


import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

public interface PatientService {
  Optional<PatientDto> getById(long id);
  List<PatientDto> getAll();
  List<PatientDto> getAllByRoomId(long roomId);
  void create(Patient patient);
  void update(Patient patient, long id);
  void deleteById(long id);
  HealthStatus getStatusById(long id);
}
