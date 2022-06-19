package ua.lviv.iot.hospital.health.api.model;

import static ua.lviv.iot.hospital.health.api.model.HealthStatus.LIGHT_SICK;
import static ua.lviv.iot.hospital.health.api.model.HealthStatus.VERY_SICK;
import static ua.lviv.iot.hospital.health.api.model.TrackerDataType.PALPITATION;
import static ua.lviv.iot.hospital.health.api.model.TrackerDataType.PRESSURE;
import static ua.lviv.iot.hospital.health.api.model.TrackerDataType.TEMPERATURE;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HealthCategory {
  HEALTHY(HealthStatus.HEALTHY, Map.of(PRESSURE, List.of(Range.of(80, 90), Range.of(130, 140)),
                                       TEMPERATURE, List.of(Range.of(36.2f,  37.1f)),
                                       PALPITATION, List.of(Range.of(60, 80)))),
  SLIGHTLY_LOWER(LIGHT_SICK, Map.of(PRESSURE, List.of(Range.of(70, 79), Range.of(110, 129)),
                                    TEMPERATURE, List.of(Range.of(35.9f, 36.1f)),
                                    PALPITATION, List.of(Range.of(45, 59)))),
  SLIGHTLY_HIGHER(LIGHT_SICK, Map.of(PRESSURE, List.of(Range.of(91, 100), Range.of(141, 150)),
                                    TEMPERATURE, List.of(Range.of(37.2f, 38.2f)),
                                    PALPITATION, List.of(Range.of(81, 90)))),
  VERY_LOWER(VERY_SICK, Map.of(PRESSURE, List.of(Range.of(40, 69), Range.of(90, 109)),
                               TEMPERATURE, List.of(Range.of(29.5f, 35.8f)),
                               PALPITATION, List.of(Range.of(25, 44)))),
  VERY_HIGHER(VERY_SICK, Map.of(PRESSURE, List.of(Range.of(101, 120), Range.of(151, 200)),
                                TEMPERATURE, List.of(Range.of(38.3f, 42.5f)),
                                PALPITATION, List.of(Range.of(91, 120))));

  private final HealthStatus status;
  private final Map<TrackerDataType, List<Range>> typeRangeMap;

}
