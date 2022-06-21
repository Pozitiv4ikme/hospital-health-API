package ua.lviv.iot.hospital.health.api.model.dto;

import java.util.List;
import lombok.Builder;

public record RoomDto(long id,
                      int number,
                      long buildingId,
                      List<PatientDto> patients) {

  @Builder
  public RoomDto {

  }
}
