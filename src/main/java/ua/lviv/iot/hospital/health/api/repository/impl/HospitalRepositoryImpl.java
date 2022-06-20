package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.exception.RepositoryNotFoundException;
import ua.lviv.iot.hospital.health.api.exception.hospital.HospitalRepositoryException;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.repository.HospitalRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.HospitalStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HospitalRepositoryImpl implements HospitalRepository {

  private final HospitalStorage hospitalStorage;

  @Override
  public void create(Hospital hospital) {
    if (getById(hospital.getId()).isPresent()) {
      String message = "Cannot create hospital, because hospital with id " + hospital.getId() + " already exists";
      log.error(message);
      throw new HospitalRepositoryException(message);
    }

    hospitalStorage.create(hospital);
  }

  @Override
  public void update(long id, Hospital hospital) {
    if (getById(id).isEmpty()) {
      String message = "Cannot update hospital, because hospital with id " + id + " does not exist";
      log.error(message);
      throw new RepositoryNotFoundException(message);
    }

    hospitalStorage.update(hospital, id);
  }

  @Override
  public void deleteById(long id) {
    hospitalStorage.deleteById(id);
  }

  @Override
  public List<Hospital> getAll() {
    return hospitalStorage.getAll();
  }

  @Override
  public Optional<Hospital> getById(long id) {
    return hospitalStorage.getById(id);
  }

}
