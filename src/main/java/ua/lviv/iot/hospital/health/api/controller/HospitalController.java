package ua.lviv.iot.hospital.health.api.controller;


import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.service.HospitalService;
import ua.lviv.iot.hospital.health.api.model.HealthCategory;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

@RestController
@RequestMapping("hospital")
@RequiredArgsConstructor
public class HospitalController {
  private final HospitalService hospitalService;

  @GetMapping
  public List<Patient> getHospital() {
    return hospitalService.getAllPatients();
  }

  @GetMapping("{status}")
  public Optional<Patient> getAllPatientsWithHealthStatus(@PathVariable("status") HealthCategory healthStatus) {
    return hospitalService.allPatientsWithHealthStatus(healthStatus);
  }

}
