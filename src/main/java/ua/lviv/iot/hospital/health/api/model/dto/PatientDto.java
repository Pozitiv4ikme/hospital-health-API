package ua.lviv.iot.hospital.health.api.model.dto;

import java.util.List;
import lombok.Builder;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;

public record PatientDto(long id,
                         String name,
                         String surname,
                         long roomId,
                         HealthStatus healthStatus,
                         List<TrackerDto> trackers) {

  @Builder
  public PatientDto {

  }
}
