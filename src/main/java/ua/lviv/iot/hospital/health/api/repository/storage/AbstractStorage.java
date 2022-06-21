package ua.lviv.iot.hospital.health.api.repository.storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractStorage {

  protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  protected static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");

  protected abstract void writeToFile();

  protected LocalDate checkUpdateDate(final LocalDate updateDate) {
    var currentDate = LocalDate.now();
    if (!updateDate.equals(currentDate)) {
      writeToFile();
      return currentDate;
    }
    return updateDate;
  }

}
