package ua.lviv.iot.hospital.health.api.exception.handler;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.lviv.iot.hospital.health.api.exception.BuildingRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.EntityNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.EntityStorageException;
import ua.lviv.iot.hospital.health.api.exception.HospitalRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.PatientRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.RoomRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerDataStorageException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerRepositoryException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerServiceException;

@RestControllerAdvice
public final class DefaultControllerAdvice {

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({EntityStorageException.class, TrackerDataStorageException.class})
  public Map<String, String> handleStorageExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getSimpleName());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({BuildingRepositoryException.class, HospitalRepositoryException.class,
      RoomRepositoryException.class, PatientRepositoryException.class, TrackerRepositoryException.class})
  public Map<String, String> handleRepositoryExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getSimpleName());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({TrackerServiceException.class})
  public Map<String, String> handleServiceExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getSimpleName());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({EntityNotFoundException.class})
  public Map<String, String> handleNotFoundExceptions(final Exception exception) {
    return Map.of("message", exception.getMessage(),
        "type", exception.getClass().getSimpleName());
  }
}
