package ua.lviv.iot.hospital.health.api.storage;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

@Component
public class TrackerStorage {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.tracker-file-pattern}")
  private String trackerFilePattern;

  @Value("${storage.tracker-file-start}")
  private String trackerFileStart;

  @Value("${storage.tracker-file-end}")
  private String trackerFileEnd;

  public void saveData(List<TrackerData> trackerDataList) {
    writeDataToFile(trackerDataList);
  }

  public List<TrackerData> getDataAll() {
    return readDataFromFiles();
  }

  public List<TrackerData> getDataByPatientId(int patientId) {
    return getDataAll().stream().filter(trackerData -> trackerData.getPatientId() == patientId).toList();
  }

  public List<TrackerData> getDataById(int id) {
    return getDataAll().stream().filter(trackerData -> trackerData.getTrackerId() == id).toList();
  }

  public void writeDataToFile(List<TrackerData> patientTrackerDataList) {
    var trackerDataFilePath = String.format(trackerFilePattern, LocalDate.now().format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + trackerDataFilePath);
    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      Writer writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND);

      StatefulBeanToCsv<TrackerData> csvWriter = new StatefulBeanToCsvBuilder<TrackerData>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(patientTrackerDataList);
      writer.close();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public List<TrackerData> readDataFromFiles() {
    var folder = new File(folderName);
    var files = folder.listFiles((d, name) -> isTrackerFile(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> parseCsvFile(file).stream()).toList();
    }
    return List.of();
  }

  private List<TrackerData> parseCsvFile(File file) {
    try {
      return new CsvToBeanBuilder<TrackerData>(Files.newBufferedReader(file.toPath()))
          .withType(TrackerData.class).build().parse();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return List.of();
  }

  private boolean isTrackerFile(String fileName) {
    return fileName.startsWith(trackerFileStart) && fileName.endsWith(trackerFileEnd);
  }

}
