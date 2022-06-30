package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.repository.storage.Storage;

@Component
@Slf4j
public class TrackerStorage extends Storage<Tracker> {

  @Value("${storage.tracker.file-pattern}")
  private String trackerFilePattern;

  @Value("${storage.tracker.file-start}")
  private String trackerFileStart;

  @Override
  protected String getFileStart() {
    return trackerFileStart;
  }

  @Override
  protected Class<Tracker> getStorageClass() {
    return Tracker.class;
  }

  @Override
  protected String getFileHeaders() {
    return Tracker.HEADERS + "\n";
  }

  @Override
  protected String getFilePattern() {
    return trackerFilePattern;
  }
}
