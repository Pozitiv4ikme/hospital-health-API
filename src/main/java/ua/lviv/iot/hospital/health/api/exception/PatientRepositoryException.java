package ua.lviv.iot.hospital.health.api.exception;

public class PatientRepositoryException extends RuntimeException {

  public PatientRepositoryException(final String message) {
    super(message);
  }
}
