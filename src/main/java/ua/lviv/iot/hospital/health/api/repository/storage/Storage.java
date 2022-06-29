package ua.lviv.iot.hospital.health.api.repository.storage;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ua.lviv.iot.hospital.health.api.exception.EntityStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Dated;
import ua.lviv.iot.hospital.health.api.model.entity.EntityId;

@Slf4j
public abstract class Storage<T extends Dated & EntityId> extends ReadStorage<T> implements MutableStorage<T> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");

  private final Map<Long, T> entities = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  private LocalDate updateDate;

  @Override
  public void create(final T entity) {
    updateDate = checkUpdateDate(updateDate);
    entities.put(entity.getId(), entity);
  }

  @Override
  public void update(final long id, final T entity) {
    entity.setUpdatedDate(updateDate);
    entities.replace(id, entity);
  }

  @Override
  public void deleteById(final long id) {
    entities.remove(id);
  }

  @Override
  public List<T> getAll() {
    return List.copyOf(entities.values());
  }

  @Override
  public Optional<T> getById(final long id) {
    return Optional.ofNullable(entities.get(id));
  }

  @Override
  @PostConstruct
  public void loadFromFiles() {
    entities.clear();
    entities.putAll(readEntitiesFromFiles().stream()
        .collect(Collectors.toMap(EntityId::getId,
            e -> e,
            (e1, e2) -> e1.getUpdatedDate().isAfter(e2.getUpdatedDate()) ? e1 : e2)));
    updateDate = LocalDate.now();
  }

  @Override
  @PreDestroy
  public void writeToFile() {
    writeEntities(getAllByDate(updateDate), updateDate);
    entities.clear();
  }

  @Override
  public void writeEntities(final List<T> entities, final LocalDate updateDate) {
    final var buildingFilePath = String.format(getFilePattern(), updateDate.format(FORMATTER));
    final var filePath = Paths.get(folderName + "/" + buildingFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (final IOException e) {
        final String message = "Unable to create file" + filePath;
        log.error(message);
        throw new EntityStorageException(message);
      }
    }
    writeEntitiesToFile(filePath.toFile(), entities);
  }

  protected abstract String getFileHeaders();

  protected abstract String getFilePattern();

  private void writeEntitiesToFile(final File file, final List<T> entities) {
    try (final var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(getFileHeaders());
      final StatefulBeanToCsv<T> csvWriter = new StatefulBeanToCsvBuilder<T>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(entities);

    } catch (final IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      final var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new EntityStorageException(message);
    }
  }

  private List<T> getAllByDate(final LocalDate date) {
    return getAll().stream()
        .filter(entity -> date.equals(entity.getUpdatedDate()))
        .toList();
  }

  private LocalDate checkUpdateDate(final LocalDate updateDate) {
    final var currentDate = LocalDate.now();
    if (!updateDate.equals(currentDate)) {
      writeToFile();
      return currentDate;
    }
    return updateDate;
  }
}
