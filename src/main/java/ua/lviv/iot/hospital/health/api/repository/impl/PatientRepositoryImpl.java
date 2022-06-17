package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.PatientRepository;
import ua.lviv.iot.hospital.health.api.storage.PatientStorage;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepository {
  private final PatientStorage patientStorage;

  @Override
  public void save(Patient patient) {
    patientStorage.save(patient);
  }

  @Override
  public List<Patient> getAll() {
    return patientStorage.getAll();
  }

  @Override
  public Optional<Patient> getById(int id) {
    return patientStorage.getById(id);
  }
}
