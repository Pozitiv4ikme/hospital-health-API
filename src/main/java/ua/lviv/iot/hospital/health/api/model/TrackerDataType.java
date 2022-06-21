package ua.lviv.iot.hospital.health.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TrackerDataType {
  PRESSURE(2),
  TEMPERATURE(1),
  PALPITATION(1);

  private final int valueAmount;

}
