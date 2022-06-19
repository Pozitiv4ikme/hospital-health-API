package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

public interface PatientRepository {
  void create(Patient patient);
  void update(long id, Patient patient);
  void deleteById(long id);
  List<Patient> getAll();
  Optional<Patient> getById(long id);
}
