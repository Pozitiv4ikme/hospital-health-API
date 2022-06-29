package ua.lviv.iot.hospital.health.api.repository.storage;


import java.io.File;
import java.util.List;

public interface ImmutableStorage<T> {

  List<T> getAll();

  void loadFromFiles();

  List<T> readEntitiesFromFiles();

  List<T> readEntitiesFromFile(final File file);

  boolean isEntityFileForRead(final String fileName);

}
