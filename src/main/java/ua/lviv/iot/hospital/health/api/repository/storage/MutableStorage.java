package ua.lviv.iot.hospital.health.api.repository.storage;

import java.util.List;
import java.util.Optional;

public interface MutableStorage<T> {

  void create(T entity);

  void update(long id, T entity);

  void deleteById(long id);

  List<T> getAll();

  Optional<T> getById(long id);

}
