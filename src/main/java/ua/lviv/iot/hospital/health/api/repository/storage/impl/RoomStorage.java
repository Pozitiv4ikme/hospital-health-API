package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.repository.storage.Storage;

@Component
@Slf4j
public final class RoomStorage extends Storage<Room> {

  @Value("${storage.room.file-pattern}")
  private String roomFilePattern;

  @Value("${storage.room.file-start}")
  private String roomFileStart;

  @Override
  protected String getFileStart() {
    return roomFileStart;
  }

  @Override
  protected Class<Room> getStorageClass() {
    return Room.class;
  }

  @Override
  protected String getFileHeaders() {
    return Room.HEADERS + "\n";
  }

  @Override
  protected String getFilePattern() {
    return roomFilePattern;
  }
}
