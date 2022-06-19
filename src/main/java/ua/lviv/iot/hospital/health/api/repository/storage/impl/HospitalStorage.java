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
import java.time.format.DateTimeFormatter;
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
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;
import ua.lviv.iot.hospital.health.api.exception.hospital.HospitalStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;

@Slf4j
@Component
public class HospitalStorage extends AbstractStorage implements MutableStorage<Hospital> {

  public static final Map<Long, Hospital> HOSPITALS = new HashMap<>();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");
  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.hospital.file-pattern}")
  private String hospitalFilePattern;

  @Value("${storage.hospital.file-start}")
  private String hospitalFileStart;

  @Value("${storage.file-end}")
  private String hospitalFileEnd;

  private LocalDate updateDate;

  @Override
  public void create(Hospital hospital) {
    updateDate = checkUpdateDate(updateDate);
    HOSPITALS.put(hospital.getId(), hospital);
  }

  @Override
  public void update(Hospital hospital, long id) {
    HOSPITALS.replace(id, hospital);
  }

  @Override
  public void deleteById(long id) {
    HOSPITALS.remove(id);
  }

  @Override
  public List<Hospital> getAll() {
    return List.copyOf(HOSPITALS.values());
  }

  @Override
  public Optional<Hospital> getById(long id) {
    return Optional.ofNullable(HOSPITALS.get(id));
  }

  @PostConstruct
  void loadFromFile() {
    HOSPITALS.clear();
    HOSPITALS.putAll(readHospitalsFromFiles().stream().collect(Collectors.toMap(Hospital::getId, h -> h)));
    updateDate = LocalDate.now();
  }

  @PreDestroy
  protected void writeToFile() {
    writeHospitals(getAll());
    HOSPITALS.clear();
  }

  public void writeHospitals(List<Hospital> hospitals) {
    var hospitalFilePath = String.format(hospitalFilePattern, updateDate.format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + hospitalFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        String message = "Unable to create file" + filePath;
        log.error(message);
        throw new HospitalStorageException(message);
      }
    }
    writeHospitalsToFile(filePath.toFile(), hospitals);
  }

  public List<Hospital> readHospitalsFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists()) {
      folder.mkdir();
    }
    var files = folder.listFiles((d, name) -> isHospitalFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readHospitalFromFile(file).stream()).toList();
    }
    return List.of();
  }

  public void writeHospitalsToFile(File file, List<Hospital> hospitals) {
    try (var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Hospital.HEADERS + "\n");
      StatefulBeanToCsv<Hospital> csvWriter = new StatefulBeanToCsvBuilder<Hospital>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(hospitals);

    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new HospitalStorageException(message);
    }
  }

  private boolean isHospitalFileForRead(String fileName) {
    return fileName.startsWith(hospitalFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(hospitalFileEnd);
  }

  private List<Hospital> readHospitalFromFile(File file) {
    try {
      return new CsvToBeanBuilder<Hospital>(Files.newBufferedReader(file.toPath()))
          .withType(Hospital.class)
          .withSkipLines(1)
          .build().parse();
    } catch (IOException e) {
      var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new HospitalStorageException(message);
    }
  }
}
