package ua.lviv.iot.hospital.health.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HealthStatus {
  UNKNOWN(0),
  HEALTHY(1),
  LIGHT_SICK(2),
  VERY_SICK(3);

  private final int priority;

}
