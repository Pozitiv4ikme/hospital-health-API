package ua.lviv.iot.hospital.health.api.exception;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(final String message) {
    super(message);
  }
}
