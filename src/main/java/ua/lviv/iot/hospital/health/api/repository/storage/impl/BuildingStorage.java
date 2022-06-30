package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.repository.storage.Storage;

@Component
@Slf4j
public final class BuildingStorage extends Storage<Building> {

  @Value("${storage.building.file-pattern}")
  private String buildingFilePattern;

  @Value("${storage.building.file-start}")
  private String buildingFileStart;

  @Override
  protected String getFileHeaders() {
    return Building.HEADERS + "\n";
  }

  @Override
  protected String getFileStart() {
    return buildingFileStart;
  }

  @Override
  protected String getFilePattern() {
    return buildingFilePattern;
  }

  @Override
  protected Class<Building> getStorageClass() {
    return Building.class;
  }
}
