package ua.lviv.iot.hospital.health.api.model.dto;

import java.util.List;
import lombok.Builder;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public record TrackerDto(long id,
                         String model,
                         HealthStatus healthStatus,
                         List<TrackerData> trackerData) {
  @Builder
  public TrackerDto {}

}
