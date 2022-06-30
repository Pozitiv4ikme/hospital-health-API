package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, V> {
  Optional<T> getById(long id);

  List<T> getAll();

  void create(V entity);

  void update(long id, V entity);

  void deleteById(long id);

}
