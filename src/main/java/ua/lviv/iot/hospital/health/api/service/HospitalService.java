package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.HealthCategory;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

public interface HospitalService {
  List<Patient> getAllPatients();
  Optional<Patient> allPatientsWithHealthStatus(HealthCategory healthStatus);
}
