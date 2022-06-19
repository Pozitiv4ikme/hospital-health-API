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
import ua.lviv.iot.hospital.health.api.exception.patient.PatientStorageException;
import ua.lviv.iot.hospital.health.api.exception.room.RoomStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Slf4j
@Component
public class PatientStorage extends AbstractStorage implements MutableStorage<Patient> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");
  private static final Map<Long, Patient> PATIENTS = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.patient.file-pattern}")
  private String patientFilePattern;

  @Value("${storage.patient.file-start}")
  private String patientFileStart;

  @Value("${storage.file-end}")
  private String fileEnd;

  private LocalDate updateDate;

  @Override
  public void create(Patient patient) {
    updateDate = checkUpdateDate(updateDate);
    PATIENTS.put(patient.getId(), patient);
  }

  @Override
  public void update(Patient patient, long id) {
    PATIENTS.replace(id, patient);
  }

  @Override
  public void deleteById(long id) {
    PATIENTS.remove(id);
  }

  @Override
  public List<Patient> getAll() {
    return List.copyOf(PATIENTS.values());
  }

  @Override
  public Optional<Patient> getById(long id) {
    return Optional.ofNullable(PATIENTS.get(id));
  }

  @PostConstruct
  void loadPatientsFromFile() {
    PATIENTS.clear();
    PATIENTS.putAll(readPatientsFromFiles().stream().collect(Collectors.toMap(Patient::getId, p -> p)));
    updateDate = LocalDate.now();
  }

  @PreDestroy
  protected void writeToFile() {
    writePatients(getAll());
    PATIENTS.clear();
  }

  public void writePatients(List<Patient> patients) {
    var patientDataFilePath = String.format(patientFilePattern, updateDate.format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + patientDataFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        String message = "Unable to create file" + filePath;
        log.error(message);
        throw new PatientStorageException(message);
      }
    }
    writePatientToFile(filePath.toFile(), patients);
  }

  public List<Patient> readPatientsFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists()) {
      folder.mkdir();
    }
    var files = folder.listFiles((d, name) -> isPatientFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readPatientFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writePatientToFile(File file, List<Patient> patients) {
    try (var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Patient.HEADERS + "\n");
      StatefulBeanToCsv<Patient> csvWriter = new StatefulBeanToCsvBuilder<Patient>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(patients);

    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new PatientStorageException(message);
    }
  }

  private boolean isPatientFileForRead(String fileName) {
    return fileName.startsWith(patientFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private List<Patient> readPatientFromFile(File file) {
    try {
      return new CsvToBeanBuilder<Patient>(Files.newBufferedReader(file.toPath()))
          .withType(Patient.class)
          .withSkipLines(1)
          .build().parse();
    } catch (IOException e) {
      var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new PatientStorageException(message);
    }
  }
}
