package ua.lviv.iot.hospital.health.api.repository.storage;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ua.lviv.iot.hospital.health.api.exception.EntityStorageException;

@Slf4j
public abstract class ReadStorage<T> implements ImmutableStorage<T> {

  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");

  @Value("${storage.file-end}")
  private String fileEnd;

  @Value("${storage.folder}")
  private String folderName;

  @Override
  public abstract List<T> getAll();

  @Override
  public abstract void loadFromFiles();

  @Override
  public boolean isEntityFileForRead(final String fileName) {
    return fileName.startsWith(getFileStart() + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  @Override
  public List<T> readEntitiesFromFiles() {
    final var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
      return List.of();
    }
    final var files = folder.listFiles((d, name) -> isEntityFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readEntitiesFromFile(file).stream()).toList();
    }
    return List.of();
  }

  @Override
  public List<T> readEntitiesFromFile(final File file) {
    try {
      return new CsvToBeanBuilder<T>(Files.newBufferedReader(file.toPath()))
          .withType(getStorageClass())
          .withSkipLines(1)
          .build().parse();
    } catch (final IOException e) {
      final var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new EntityStorageException(message);
    }
  }

  protected abstract String getFileStart();

  protected abstract Class<T> getStorageClass();

}
