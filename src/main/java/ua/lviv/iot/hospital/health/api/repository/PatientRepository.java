package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

public interface PatientRepository {
  void save(Patient patient);
  List<Patient> getAll();
  Optional<Patient> getById(int id);
}
