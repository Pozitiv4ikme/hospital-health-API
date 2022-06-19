package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;

public interface HospitalRepository {
  void create(Hospital hospital);
  void update(Hospital hospital, long id);
  void deleteById(long id);
  List<Hospital> getAll();
  Optional<Hospital> getById(long id);
}
