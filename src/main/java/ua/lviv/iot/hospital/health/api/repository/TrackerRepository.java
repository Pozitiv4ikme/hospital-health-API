package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;

public interface TrackerRepository {

  void create(Tracker tracker);

  void update(long id, Tracker tracker);

  void deleteById(long id);

  List<Tracker> getAll();

  Optional<Tracker> getById(long id);
}
