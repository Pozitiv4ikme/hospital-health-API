package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.repository.storage.Storage;

@Slf4j
@Component
public final class HospitalStorage extends Storage<Hospital> {

  @Value("${storage.hospital.file-pattern}")
  private String hospitalFilePattern;

  @Value("${storage.hospital.file-start}")
  private String hospitalFileStart;

  @Override
  protected String getFileHeaders() {
    return Hospital.HEADERS + "\n";
  }

  @Override
  protected String getFileStart() {
    return hospitalFileStart;
  }

  @Override
  protected String getFilePattern() {
    return hospitalFilePattern;
  }

  @Override
  protected Class<Hospital> getStorageClass() {
    return Hospital.class;
  }
}
