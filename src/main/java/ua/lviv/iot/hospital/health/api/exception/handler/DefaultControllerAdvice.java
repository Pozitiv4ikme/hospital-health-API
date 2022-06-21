package ua.lviv.iot.hospital.health.api.exception.handler;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.lviv.iot.hospital.health.api.exception.RepositoryNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.building.BuildingRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.building.BuildingStorageException;
import ua.lviv.iot.hospital.health.api.exception.hospital.HospitalRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.hospital.HospitalStorageException;
import ua.lviv.iot.hospital.health.api.exception.patient.PatientRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.patient.PatientStorageException;
import ua.lviv.iot.hospital.health.api.exception.room.RoomRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.room.RoomStorageException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerDataStorageException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerServiceException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerStorageException;

@RestControllerAdvice
public final class DefaultControllerAdvice {

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({BuildingStorageException.class, HospitalStorageException.class,
      PatientStorageException.class, RoomStorageException.class, TrackerStorageException.class,
      TrackerDataStorageException.class})
  public Map<String, String> handleStorageExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
                  "type", exception.getClass().getName());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({BuildingRepositoryException.class, HospitalRepositoryException.class,
      RoomRepositoryException.class, PatientRepositoryException.class, TrackerRepositoryException.class})
  public Map<String, String> handleRepositoryExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getName());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({TrackerServiceException.class})
  public Map<String, String> handleServiceExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getName());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({RepositoryNotFoundException.class})
  public Map<String, String> handleNotFoundExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getName());
  }
}
