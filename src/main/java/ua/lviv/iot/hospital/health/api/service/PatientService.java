package ua.lviv.iot.hospital.health.api.service;


import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface PatientService {
  List<Patient> getAll();
  Optional<Patient> getById(int id);
  HealthStatus getStatusById(int id);
}
