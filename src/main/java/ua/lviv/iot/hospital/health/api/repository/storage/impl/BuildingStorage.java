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
import ua.lviv.iot.hospital.health.api.exception.building.BuildingStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Building;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Component
@Slf4j
public class BuildingStorage extends AbstractStorage implements MutableStorage<Building> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_");
  private static final Map<Long, Building> BUILDINGS = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.building.file-pattern}")
  private String buildingFilePattern;

  @Value("${storage.building.file-start}")
  private String buildingFileStart;

  @Value("${storage.file-end}")
  private String buildingFileEnd;

  private LocalDate updateDate;

  @Override
  public void create(Building building) {
    updateDate = checkUpdateDate(updateDate);
    BUILDINGS.put(building.getId(), building);
  }

  @Override
  public void update(Building building, long id) {
    BUILDINGS.replace(id, building);
  }

  @Override
  public void deleteById(long id) {
    BUILDINGS.remove(id);
  }

  @Override
  public List<Building> getAll() {
    return List.copyOf(BUILDINGS.values());
  }

  @Override
  public Optional<Building> getById(long id) {
    return Optional.ofNullable(BUILDINGS.get(id));
  }

  @PostConstruct
  void loadFromFile() {
    BUILDINGS.clear();
    BUILDINGS.putAll(readBuildingsFromFiles().stream().collect(Collectors.toMap(Building::getId, b -> b)));
    updateDate = LocalDate.now();
  }

  @PreDestroy
  protected void writeToFile() {
    writeBuildings(getAll());
    BUILDINGS.clear();
  }

  public void writeBuildings(List<Building> buildings) {
    var buildingFilePath = String.format(buildingFilePattern, updateDate.format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + buildingFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        String message = "Unable to create file" + filePath;
        log.error(message);
        throw new BuildingStorageException(message);
      }
    }
    writeBuildingsToFile(filePath.toFile(), buildings);
  }

  public List<Building> readBuildingsFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists()) {
      folder.mkdir();
    }
    var files = folder.listFiles((d, name) -> isBuildingFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readBuildingFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writeBuildingsToFile(File file, List<Building> buildings) {
    try (var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Building.HEADERS + "\n");
      StatefulBeanToCsv<Building> csvWriter = new StatefulBeanToCsvBuilder<Building>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(buildings);

    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new BuildingStorageException(message);
    }
  }

  private boolean isBuildingFileForRead(String fileName) {
    return fileName.startsWith(buildingFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(buildingFileEnd);
  }

  private List<Building> readBuildingFromFile(File file) {
    try {
      return new CsvToBeanBuilder<Building>(Files.newBufferedReader(file.toPath()))
          .withType(Building.class)
          .withSkipLines(1)
          .build().parse();
    } catch (IOException e) {
      var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new BuildingStorageException(message);
    }
  }
}
