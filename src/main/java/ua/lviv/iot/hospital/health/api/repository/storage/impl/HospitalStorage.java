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
import ua.lviv.iot.hospital.health.api.exception.hospital.HospitalStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Slf4j
@Component
public final class HospitalStorage extends AbstractStorage implements MutableStorage<Hospital> {

  private static final Map<Long, Hospital> HOSPITALS = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.hospital.file-pattern}")
  private String hospitalFilePattern;

  @Value("${storage.hospital.file-start}")
  private String hospitalFileStart;

  @Value("${storage.file-end}")
  private String fileEnd;

  private LocalDate updateDate;

  @Override
  public void create(final Hospital hospital) {
    updateDate = checkUpdateDate(updateDate);
    HOSPITALS.put(hospital.getId(), hospital);
  }

  @Override
  public void update(final long id, final Hospital hospital) {
    hospital.setUpdatedDate(updateDate);
    HOSPITALS.replace(id, hospital);
  }

  @Override
  public void deleteById(final long id) {
    HOSPITALS.remove(id);
  }

  @Override
  public List<Hospital> getAll() {
    return List.copyOf(HOSPITALS.values());
  }

  @Override
  public Optional<Hospital> getById(final long id) {
    return Optional.ofNullable(HOSPITALS.get(id));
  }

  @PostConstruct
  void loadFromFiles() {
    HOSPITALS.putAll(readHospitalsFromFiles()
        .stream()
        .collect(Collectors.toMap(Hospital::getId,
            h -> h,
            (h1, h2) -> h1.getUpdatedDate().isAfter(h2.getUpdatedDate()) ? h1 : h2)));
    updateDate = LocalDate.now();
  }

  @Override
  @PreDestroy
  protected void writeToFile() {
    writeHospitals(getAllByDate(updateDate), updateDate);
    HOSPITALS.clear();
  }

  void writeHospitals(final List<Hospital> hospitals, final LocalDate updateDate) {
    final var hospitalFilePath = String.format(hospitalFilePattern, updateDate.format(FORMATTER));
    final var filePath = Paths.get(folderName + "/" + hospitalFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (final IOException e) {
        final String message = "Unable to create file" + filePath;
        log.error(message);
        throw new HospitalStorageException(message);
      }
    }
    writeHospitalsToFile(filePath.toFile(), hospitals);
  }

  List<Hospital> readHospitalsFromFiles() {
    final var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
      return List.of();
    }
    final var files = folder.listFiles((d, name) -> isHospitalFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readHospitalsFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writeHospitalsToFile(final File file, final List<Hospital> hospitals) {
    try (final var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Hospital.HEADERS + "\n");
      final StatefulBeanToCsv<Hospital> csvWriter = new StatefulBeanToCsvBuilder<Hospital>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(hospitals);

    } catch (final IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      final var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new HospitalStorageException(message);
    }
  }

  private boolean isHospitalFileForRead(final String fileName) {
    return fileName.startsWith(hospitalFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private List<Hospital> readHospitalsFromFile(final File file) {
    try {
      return new CsvToBeanBuilder<Hospital>(Files.newBufferedReader(file.toPath()))
          .withType(Hospital.class)
          .withSkipLines(1)
          .build().parse();
    } catch (final IOException e) {
      final var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new HospitalStorageException(message);
    }
  }

  private List<Hospital> getAllByDate(final LocalDate date) {
    return getAll().stream()
        .filter(hospital -> date.equals(hospital.getUpdatedDate()))
        .toList();
  }
}
