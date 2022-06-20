package ua.lviv.iot.hospital.health.api.model.dto;

import java.util.List;
import lombok.Builder;

public record BuildingDto(long id,
                          String name,
                          String address,
                          long hospitalId,
                          List<RoomDto> rooms) {

  @Builder
  public BuildingDto {

  }
}
