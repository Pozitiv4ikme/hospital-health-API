package ua.lviv.iot.hospital.health.api.exception.patient;

public class PatientRepositoryException extends RuntimeException {

  public PatientRepositoryException(String message) {
    super(message);
  }
}
