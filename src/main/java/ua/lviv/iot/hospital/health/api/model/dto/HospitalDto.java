package ua.lviv.iot.hospital.health.api.model.dto;

import java.util.List;
import lombok.Builder;

public record HospitalDto(long id,
                          String name,
                          List<BuildingDto> buildings) {

  @Builder
  public HospitalDto {

  }
}
