package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Component
@Slf4j
public class TrackerStorage extends AbstractStorage implements MutableStorage<Tracker> {

  private static final Map<Long, Tracker> TRACKERS = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.tracker.file-pattern}")
  private String trackerFilePattern;

  @Value("${storage.tracker.file-start}")
  private String trackerFileStart;

  @Value("${storage.file-end}")
  private String fileEnd;

  private LocalDate updateDate;

  @Override
  public void create(final Tracker tracker) {
    updateDate = checkUpdateDate(updateDate);
    TRACKERS.put(tracker.getId(), tracker);
  }

  @Override
  public void update(final long id, final Tracker tracker) {
    tracker.setUpdatedDate(updateDate);
    TRACKERS.replace(id, tracker);
  }

  @Override
  public void deleteById(final long id) {
    TRACKERS.remove(id);
  }

  @Override
  public List<Tracker> getAll() {
    return List.copyOf(TRACKERS.values());
  }

  @Override
  public Optional<Tracker> getById(final long id) {
    return Optional.ofNullable(TRACKERS.get(id));
  }

  @PostConstruct
  void loadFromFiles() {
    TRACKERS.clear();
    TRACKERS.putAll(readTrackersFromFiles().stream()
        .collect(Collectors.toMap(Tracker::getId,
            t -> t,
            (t1, t2) -> t1.getUpdatedDate().isAfter(t2.getUpdatedDate()) ? t1 : t2)));
    updateDate = LocalDate.now();
  }

  @Override
  @PreDestroy
  protected void writeToFile() {
    writeTrackers(getAllByDate(updateDate), updateDate);
    TRACKERS.clear();
  }

  void writeTrackers(final List<Tracker> trackers, final LocalDate updateDate) {
    final var trackerFilePath = String.format(trackerFilePattern, updateDate.format(FORMATTER));
    final var filePath = Paths.get(folderName + "/" + trackerFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (final IOException e) {
        final String message = "Unable to create file" + filePath;
        log.error(message);
        throw new TrackerStorageException(message);
      }
    }
    writeTrackersToFile(filePath.toFile(), trackers);
  }

  List<Tracker> readTrackersFromFiles() {
    final var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
      return List.of();
    }
    final var files = folder.listFiles((d, name) -> isTrackerFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readTrackersFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writeTrackersToFile(final File file, final List<Tracker> trackers) {
    try (final var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Tracker.HEADERS + "\n");
      final StatefulBeanToCsv<Tracker> csvWriter = new StatefulBeanToCsvBuilder<Tracker>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(trackers);

    } catch (final IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      final var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new TrackerStorageException(message);
    }
  }

  private boolean isTrackerFileForRead(final String fileName) {
    return fileName.startsWith(trackerFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private List<Tracker> readTrackersFromFile(final File file) {
    try {
      return new CsvToBeanBuilder<Tracker>(Files.newBufferedReader(file.toPath()))
          .withType(Tracker.class)
          .withSkipLines(1)
          .build().parse();
    } catch (final IOException e) {
      final var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new TrackerStorageException(message);
    }
  }

  private List<Tracker> getAllByDate(final LocalDate date) {
    return getAll().stream()
        .filter(tracker -> date.equals(tracker.getUpdatedDate()))
        .toList();
  }

}
