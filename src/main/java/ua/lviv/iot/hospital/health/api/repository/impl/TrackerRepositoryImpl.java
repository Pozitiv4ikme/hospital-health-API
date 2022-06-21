package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.RepositoryNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.repository.TrackerRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.TrackerStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public final class TrackerRepositoryImpl implements TrackerRepository {

  private final TrackerStorage trackerStorage;

  @Override
  public void create(final Tracker tracker) {
    if (getById(tracker.getId()).isPresent()) {
      final String message = "Cannot create tracker, because tracker with id " + tracker.getId() + " already exists";
      log.error(message);
      throw new TrackerRepositoryException(message);
    }

    trackerStorage.create(tracker);
  }

  @Override
  public void update(final long id, final Tracker tracker) {
    if (getById(id).isEmpty()) {
      final String message = "Cannot update tracker, because tracker with id " + id + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }

    trackerStorage.update(id, tracker);
  }

  @Override
  public void deleteById(final long id) {
    trackerStorage.deleteById(id);
  }

  @Override
  public List<Tracker> getAll() {
    return trackerStorage.getAll();
  }

  @Override
  public Optional<Tracker> getById(final long id) {
    return trackerStorage.getById(id);
  }


}
