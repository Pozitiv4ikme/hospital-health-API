package ua.lviv.iot.hospital.health.api.exception;

public class RepositoryNotFoundException extends RuntimeException {

  public RepositoryNotFoundException(String message) {
    super(message);
  }
}
