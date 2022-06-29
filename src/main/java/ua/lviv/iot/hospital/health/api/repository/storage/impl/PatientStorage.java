package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.storage.Storage;

@Slf4j
@Component
public final class PatientStorage extends Storage<Patient> {

  @Value("${storage.patient.file-pattern}")
  private String patientFilePattern;

  @Value("${storage.patient.file-start}")
  private String patientFileStart;

  @Override
  protected String getFileHeaders() {
    return Patient.HEADERS + "\n";
  }

  @Override
  protected String getFileStart() {
    return patientFileStart;
  }

  @Override
  protected String getFilePattern() {
    return patientFilePattern;
  }

  @Override
  protected Class<Patient> getStorageClass() {
    return Patient.class;
  }
}
