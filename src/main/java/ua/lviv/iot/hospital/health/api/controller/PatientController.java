package ua.lviv.iot.hospital.health.api.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.TrackerService;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

@RestController
@RequestMapping("patients")
@RequiredArgsConstructor
public class PatientController {

  private final PatientService patientService;
  private final TrackerService trackerService;

  @GetMapping
  public List<Patient> getAllPatients() {
    return patientService.getAll();
  }

  @GetMapping("{id}")
  public Optional<Patient> getPatientById(@PathVariable("id") int id) {
    return patientService.getById(id);
  }

  @GetMapping("{id}/status")
  public HealthStatus getPatientStatusById(@PathVariable("id") int id) {
    return patientService.getStatusById(id);
  }

  @GetMapping("{id}/trackerData")
  public List<TrackerData> patientTrackerData(@PathVariable("id") int patientId) {
    return trackerService.getTrackerDataByPatientId(patientId);
  }

  @PostMapping("{id}/trackerData")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public HealthStatus addPatientTrackerData(@PathVariable("id") int patientId,
      @RequestBody List<TrackerData> trackerDataList) throws Exception {
    trackerService.addTrackerData(patientId, trackerDataList);
    return trackerService.getHealthStatus(trackerDataList);
  }
}
