package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
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
import java.util.Arrays;
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
import ua.lviv.iot.hospital.health.api.repository.storage.ImmutableStorage;

@Slf4j
@Component
public final class TrackerDataStorage implements ImmutableStorage {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");
  private static final Map<Long, List<TrackerData>> PATIENT_TRACKER_DATA = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.tracker.data.file-pattern}")
  private String trackerFilePattern;

  @Value("${storage.tracker.data.file-start}")
  private String trackerFileStart;

  @Value("${storage.file-end}")
  private String fileEnd;

  public void saveData(final long patientId, final List<TrackerData> trackerDataList) {
    trackerDataList.forEach(trackerData -> trackerData.setPatientId(patientId));
    saveDataToMap(patientId, trackerDataList);
    writeDataToFile(trackerDataList, LocalDate.now());
  }

  public List<TrackerData> getDataAll() {
    return PATIENT_TRACKER_DATA.values()
        .stream()
        .flatMap(Collection::stream)
        .toList();
  }

  public List<TrackerData> getDataByPatientId(final long patientId) {
    return PATIENT_TRACKER_DATA.get(patientId);
  }

  public List<TrackerData> getDataByTrackerId(final long id) {
    log.info("getting tracker data for id: " + id);
    return getDataAll().stream().filter(trackerData -> trackerData.getTrackerId() == id).toList();
  }

  void writeDataToFile(final List<TrackerData> patientTrackerDataList, final LocalDate date) {
    var trackerDataFilePath = String.format(trackerFilePattern, date.format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + trackerDataFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
        writeHeader(filePath.toFile());
      } catch (IOException e) {
        String message = "Unable to create file" + trackerDataFilePath;
        log.error(message);
        throw new TrackerDataStorageException(message);
      }
    }

    try (var writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {

      StatefulBeanToCsv<TrackerData> csvWriter = new StatefulBeanToCsvBuilder<TrackerData>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(patientTrackerDataList);

    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      var message = "Unable to write file " + filePath;
      log.error(message);
      throw new TrackerDataStorageException(message);
    }
  }

  List<TrackerData> readDataFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
        return List.of();
    }
    var files = folder.listFiles((d, name) -> isDataFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readDataFromFile(file).stream()).toList();
    }
    return List.of();
  }

  @PostConstruct
  void loadDataFromFiles() {
    PATIENT_TRACKER_DATA.clear();
    PATIENT_TRACKER_DATA.putAll(readDataFromFiles()
        .stream()
        .collect(Collectors.groupingBy(TrackerData::getPatientId)));
  }

  private List<TrackerData> readDataFromFile(final File file) {
    try {
      return new CsvToBeanBuilder<TrackerData>(Files.newBufferedReader(file.toPath()))
          .withType(TrackerData.class)
          .withSkipLines(1)
          .build().parse();
    } catch (IOException e) {
      var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new TrackerDataStorageException(message);
    }
  }

  private boolean isDataFileForRead(final String fileName) {
    return fileName.startsWith(trackerFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private void saveDataToMap(final long patientId, final List<TrackerData> trackerDataList) {
    if (PATIENT_TRACKER_DATA.containsKey(patientId)) {
      PATIENT_TRACKER_DATA.get(patientId).addAll(trackerDataList);
    } else {
      PATIENT_TRACKER_DATA.put(patientId, new ArrayList<>(trackerDataList));
    }
  }

  private void writeHeader(File file) {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      writer.write(TrackerData.HEADERS + "\n");
    } catch (IOException e) {
      var message = "Unable to write header for" + file.getPath();
      log.error(message);
      throw new TrackerDataStorageException(message);
    }
  }

}
