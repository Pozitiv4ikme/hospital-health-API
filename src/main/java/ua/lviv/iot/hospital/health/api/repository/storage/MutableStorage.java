package ua.lviv.iot.hospital.health.api.repository.storage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MutableStorage<T> extends ImmutableStorage<T> {

  void create(T entity);

  void update(long id, T entity);

  void deleteById(long id);

  Optional<T> getById(long id);

  void writeToFile();

  void writeEntities(final List<T> entities, final LocalDate updateDate);
}
