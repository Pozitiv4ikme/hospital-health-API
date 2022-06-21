package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
  void create(T entity);

  void update(long id, T entity);

  void deleteById(long id);

  List<T> getAll();

  Optional<T> getById(long id);
}
