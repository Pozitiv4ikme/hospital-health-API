package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerDataStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.repository.storage.ReadStorage;

@Slf4j
@Component
public final class TrackerDataStorage extends ReadStorage<TrackerData> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  private static final Map<Long, List<TrackerData>> PATIENT_TRACKER_DATA = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.tracker.data.file-pattern}")
  private String trackerFilePattern;

  @Value("${storage.tracker.data.file-start}")
  private String trackerFileStart;

  @Override
  public List<TrackerData> getAll() {
    return PATIENT_TRACKER_DATA.values()
        .stream()
        .flatMap(Collection::stream)
        .toList();
  }

  public List<TrackerData> getByPatientId(final long patientId) {
    return PATIENT_TRACKER_DATA.get(patientId);
  }

  public List<TrackerData> getByTrackerId(final long id) {
    log.info("getting tracker data for id: " + id);
    return getAll().stream().filter(trackerData -> trackerData.getTrackerId() == id).toList();
  }

  public void save(final long patientId, final List<TrackerData> trackerDataList) {
    trackerDataList.forEach(trackerData -> trackerData.setPatientId(patientId));
    saveToMap(patientId, trackerDataList);
    writeEntitiesToFile(trackerDataList, LocalDate.now());
  }

  @Override
  @PostConstruct
  public void loadFromFiles() {
    PATIENT_TRACKER_DATA.clear();
    PATIENT_TRACKER_DATA.putAll(readEntitiesFromFiles()
        .stream()
        .collect(Collectors.groupingBy(TrackerData::getPatientId)));
  }

  void writeEntitiesToFile(final List<TrackerData> patientTrackerDataList, final LocalDate date) {
    final var trackerDataFilePath = String.format(trackerFilePattern, date.format(FORMATTER));
    final var filePath = Paths.get(folderName + "/" + trackerDataFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
        writeHeader(filePath.toFile());
      } catch (final IOException e) {
        final String message = "Unable to create file" + trackerDataFilePath;
        log.error(message);
        throw new TrackerDataStorageException(message);
      }
    }

    try (final var writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {

      final StatefulBeanToCsv<TrackerData> csvWriter = new StatefulBeanToCsvBuilder<TrackerData>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(patientTrackerDataList);

    } catch (final IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      final var message = "Unable to write file " + filePath;
      log.error(message);
      throw new TrackerDataStorageException(message);
    }
  }

  @Override
  protected String getFileStart() {
    return trackerFileStart;
  }

  @Override
  protected Class<TrackerData> getStorageClass() {
    return TrackerData.class;
  }

  private void saveToMap(final long patientId, final List<TrackerData> trackerDataList) {
    if (PATIENT_TRACKER_DATA.containsKey(patientId)) {
      PATIENT_TRACKER_DATA.get(patientId).addAll(trackerDataList);
    } else {
      PATIENT_TRACKER_DATA.put(patientId, new ArrayList<>(trackerDataList));
    }
  }

  private void writeHeader(final File file) {
    try (final Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      writer.write(TrackerData.HEADERS + "\n");
    } catch (final IOException e) {
      final var message = "Unable to write header for" + file.getPath();
      log.error(message);
      throw new TrackerDataStorageException(message);
    }
  }

}
