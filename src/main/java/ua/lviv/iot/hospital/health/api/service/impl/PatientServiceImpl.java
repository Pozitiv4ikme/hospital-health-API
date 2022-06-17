package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.PatientRepository;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
  private final TrackerService trackerService;
  private final PatientRepository patientRepository;

  @Override
  public List<Patient> getAll() {
    return patientRepository.getAll();
  }

  @Override
  public Optional<Patient> getById(int id) {
    return patientRepository.getById(id);
  }

  @Override
  public HealthStatus getStatusById(int id) {
    return trackerService.getHealthStatus(trackerService.getTrackerDataByPatientId(id));
  }

}
