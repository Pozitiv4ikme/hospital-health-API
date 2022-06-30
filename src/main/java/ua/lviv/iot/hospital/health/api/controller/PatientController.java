package ua.lviv.iot.hospital.health.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@RestController
@RequestMapping("patients")
@RequiredArgsConstructor
public final class PatientController {

  private final PatientService patientService;
  private final TrackerService trackerService;

  @PostMapping
  public void create(@RequestBody final Patient patient) {
    patientService.create(patient);
  }

  @PutMapping("{id}")
  public void update(@PathVariable("id") final long id, @RequestBody final Patient patient) {
    patientService.update(id, patient);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable("id") final long id) {
    patientService.deleteById(id);
  }

  @GetMapping
  public List<PatientDto> getAll() {
    return patientService.getAll();
  }

  @GetMapping("{id}")
  public ResponseEntity<PatientDto> getById(@PathVariable("id") final long id) {
    return patientService.getById(id)
        .map(patientDto -> ResponseEntity.ok().body(patientDto))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("{id}/status")
  public HealthStatus getPatientStatusById(@PathVariable("id") final long id) {
    return patientService.getStatusById(id);
  }

  @GetMapping("{id}/trackerData")
  public List<TrackerData> getPatientTrackerData(@PathVariable("id") final long patientId) {
    return trackerService.getDataByPatientId(patientId);
  }

  @PostMapping("{id}/trackerData")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public HealthStatus addPatientTrackerData(@PathVariable("id") final long patientId,
      @RequestBody final List<TrackerData> trackerDataList) throws Exception {
    trackerService.addData(patientId, trackerDataList);
    return trackerService.getHealthStatus(trackerDataList);
  }
}
