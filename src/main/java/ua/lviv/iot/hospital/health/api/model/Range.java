package ua.lviv.iot.hospital.health.api.model;

public record Range(float min, float max) {
  public static Range of(float min, float max) {
    return new Range(min, max);
  }
}
