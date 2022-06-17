package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.service.HospitalService;
import ua.lviv.iot.hospital.health.api.model.HealthCategory;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

@Service
public class HospitalServiceImpl implements HospitalService {

  @Override
  public List<Patient> getAllPatients() {
    return List.of();
  }

  @Override
  public Optional<Patient> allPatientsWithHealthStatus(HealthCategory healthStatus) {
    return Optional.empty();
  }
}
