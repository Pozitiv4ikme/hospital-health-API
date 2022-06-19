package ua.lviv.iot.hospital.health.api.repository.storage;

import java.time.LocalDate;

public abstract class AbstractStorage {

  protected abstract void writeToFile();

  protected LocalDate checkUpdateDate(LocalDate updateDate) {
    var currentDate = LocalDate.now();
    if (!updateDate.equals(currentDate)) {
      writeToFile();
      return currentDate;
    }
    return updateDate;
  }

}
