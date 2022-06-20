package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.RepositoryNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.patient.PatientRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.PatientRepository;
import ua.lviv.iot.hospital.health.api.repository.RoomRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.PatientStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public final class PatientRepositoryImpl implements PatientRepository {

  private final PatientStorage patientStorage;
  private final RoomRepository roomRepository;

  @Override
  public void create(final Patient patient) {
    if (getById(patient.getId()).isPresent()) {
      final String message = "Cannot create patient, because patient with id " + patient.getId() + " already exists";
      log.error(message);
      throw new PatientRepositoryException(message);
    }

    doesRoomExist(patient);
    patientStorage.create(patient);
  }

  @Override
  public void update(final long id, final Patient patient) {
    if (getById(id).isEmpty()) {
      final String message = "Cannot update patient, because patient with id " + id + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }

    doesRoomExist(patient);
    patientStorage.update(id, patient);
  }

  @Override
  public void deleteById(final long id) {
    patientStorage.deleteById(id);
  }

  @Override
  public List<Patient> getAll() {
    return patientStorage.getAll();
  }

  @Override
  public Optional<Patient> getById(final long id) {
    return patientStorage.getById(id);
  }

  private void doesRoomExist(final Patient patient) {
    if (roomRepository.getById(patient.getRoomId()).isEmpty()) {
      final String message = "Room with id " + patient.getRoomId() + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }
  }

}
