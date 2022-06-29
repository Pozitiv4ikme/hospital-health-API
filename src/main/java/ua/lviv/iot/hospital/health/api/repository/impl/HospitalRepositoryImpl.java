package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.EntityNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.HospitalRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.repository.HospitalRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.HospitalStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public final class HospitalRepositoryImpl implements HospitalRepository {

  private final HospitalStorage hospitalStorage;

  @Override
  public void create(final Hospital hospital) {
    if (getById(hospital.getId()).isPresent()) {
      final String message = "Cannot create hospital, because hospital with id " + hospital.getId() + " already exists";
      log.error(message);
      throw new HospitalRepositoryException(message);
    }

    hospitalStorage.create(hospital);
  }

  @Override
  public void update(final long id, final Hospital hospital) {
    if (getById(id).isEmpty()) {
      final String message = "Cannot update hospital, because hospital with id " + id + " does not exist";
      log.error(message);
      throw new EntityNotFoundException(message);
    }

    hospitalStorage.update(id, hospital);
  }

  @Override
  public void deleteById(final long id) {
    hospitalStorage.deleteById(id);
  }

  @Override
  public List<Hospital> getAll() {
    return hospitalStorage.getAll();
  }

  @Override
  public Optional<Hospital> getById(final long id) {
    return hospitalStorage.getById(id);
  }

}
