package ua.lviv.iot.hospital.health.api.exception;

public class RepositoryNotFoundException extends RuntimeException {

  public RepositoryNotFoundException(final String message) {
    super(message);
  }
}
