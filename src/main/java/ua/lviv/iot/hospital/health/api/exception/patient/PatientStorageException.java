package ua.lviv.iot.hospital.health.api.exception.patient;

public class PatientStorageException extends RuntimeException {
  public PatientStorageException(String message) {
    super(message);
  }
}
