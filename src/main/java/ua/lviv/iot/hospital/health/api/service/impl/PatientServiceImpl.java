package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.dto.TrackerDto;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.PatientRepository;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

  private final PatientRepository patientRepository;
  private final TrackerService trackerService;

  @Override
  public Optional<PatientDto> getById(long id) {
    return patientRepository.getById(id)
        .map(patient -> buildPatientDto(patient, trackerService.getAllByPatientId(id)));
  }

  @Override
  public List<PatientDto> getAll() {
    return patientRepository.getAll().stream()
        .map(patient -> buildPatientDto(patient, trackerService.getAllByPatientId(patient.getId())))
        .toList();
  }

  @Override
  public List<PatientDto> getAllByRoomId(long roomId) {
    return getAll().stream()
        .filter(patientDto -> patientDto.roomId() == roomId)
        .toList();
  }

  @Override
  public void create(Patient patient) {
    patientRepository.create(patient);
  }

  @Override
  public void update(Patient patient, long id) {
    patientRepository.update(id, patient);
  }

  @Override
  public void deleteById(long id) {
    patientRepository.deleteById(id);
  }

  @Override
  public HealthStatus getStatusById(long id) {
    return trackerService.getHealthStatus(trackerService.getDataByPatientId(id));
  }

  private PatientDto buildPatientDto(Patient patient, List<TrackerDto> trackers) {
    return PatientDto.builder()
        .id(patient.getId())
        .name(patient.getName())
        .surname(patient.getSurname())
        .roomId(patient.getRoomId())
        .healthStatus(getHealthStatus(trackers))
        .trackers(trackers)
        .build();
  }

  private HealthStatus getHealthStatus(List<TrackerDto> trackers) {
    return trackerService.getHealthStatus(trackers.stream()
        .flatMap(trackerDto -> trackerDto.trackerData().stream())
        .toList());
  }

}
