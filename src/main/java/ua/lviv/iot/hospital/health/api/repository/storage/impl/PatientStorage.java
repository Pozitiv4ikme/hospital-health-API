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
import ua.lviv.iot.hospital.health.api.exception.patient.PatientStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Slf4j
@Component
public final class PatientStorage extends AbstractStorage implements MutableStorage<Patient> {

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
  public void create(final Patient patient) {
    updateDate = checkUpdateDate(updateDate);
    PATIENTS.put(patient.getId(), patient);
  }

  @Override
  public void update(final Patient patient, final long id) {
    PATIENTS.replace(id, patient);
  }

  @Override
  public void deleteById(final long id) {
    PATIENTS.remove(id);
  }

  @Override
  public List<Patient> getAll() {
    return List.copyOf(PATIENTS.values());
  }

  @Override
  public Optional<Patient> getById(final long id) {
    return Optional.ofNullable(PATIENTS.get(id));
  }

  @PostConstruct
  void loadFromFiles() {
    PATIENTS.clear();
    PATIENTS.putAll(readPatientsFromFiles().stream()
        .collect(Collectors.toMap(Patient::getId,
            p -> p,
            (p1, p2) -> p1.getUpdatedDate().isAfter(p2.getUpdatedDate()) ? p1 : p2)));
    updateDate = LocalDate.now();
  }

  @PreDestroy
  protected void writeToFile() {
    writePatients(getAllByDate(updateDate), updateDate);
    PATIENTS.clear();
  }

  void writePatients(final List<Patient> patients, final LocalDate updateDate) {
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
    writePatientsToFile(filePath.toFile(), patients);
  }

  List<Patient> readPatientsFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
      return List.of();
    }
    var files = folder.listFiles((d, name) -> isPatientFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readPatientsFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writePatientsToFile(final File file, final List<Patient> patients) {
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

  private boolean isPatientFileForRead(final String fileName) {
    return fileName.startsWith(patientFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private List<Patient> readPatientsFromFile(final File file) {
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

  private List<Patient> getAllByDate(LocalDate date) {
    return getAll().stream()
        .filter(patient -> date.equals(patient.getUpdatedDate()))
        .toList();
  }
}
