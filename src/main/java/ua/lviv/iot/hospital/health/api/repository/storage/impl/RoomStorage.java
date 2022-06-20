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
import ua.lviv.iot.hospital.health.api.exception.room.RoomStorageException;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.repository.storage.AbstractStorage;
import ua.lviv.iot.hospital.health.api.repository.storage.MutableStorage;

@Component
@Slf4j
public final class RoomStorage extends AbstractStorage implements MutableStorage<Room> {

  private static final Map<Long, Room> ROOMS = new HashMap<>();

  @Value("${storage.folder}")
  private String folderName;

  @Value("${storage.room.file-pattern}")
  private String roomFilePattern;

  @Value("${storage.room.file-start}")
  private String roomFileStart;

  @Value("${storage.file-end}")
  private String fileEnd;

  private LocalDate updateDate;

  @Override
  public void create(final Room room) {
    updateDate = checkUpdateDate(updateDate);
    ROOMS.put(room.getId(), room);
  }

  @Override
  public void update(final Room room, final long id) {
    room.setUpdatedDate(updateDate);
    ROOMS.replace(id, room);
  }

  @Override
  public void deleteById(final long id) {
    ROOMS.remove(id);
  }

  @Override
  public List<Room> getAll() {
    return List.copyOf(ROOMS.values());
  }

  @Override
  public Optional<Room> getById(final long id) {
    return Optional.ofNullable(ROOMS.get(id));
  }

  @PostConstruct
  void loadFromFiles() {
    ROOMS.clear();
    ROOMS.putAll(readRoomsFromFiles().stream()
        .collect(Collectors.toMap(Room::getId,
            r -> r,
            (r1, r2) -> r1.getUpdatedDate().isAfter(r2.getUpdatedDate()) ? r1 : r2)));
    updateDate = LocalDate.now();
  }

  @PreDestroy
  protected void writeToFile() {
    writeRooms(getAllByDate(updateDate), updateDate);
    ROOMS.clear();
  }

  void writeRooms(final List<Room> rooms, final LocalDate updateDate) {
    var roomFilePath = String.format(roomFilePattern, updateDate.format(FORMATTER));
    var filePath = Paths.get(folderName + "/" + roomFilePath);

    if (Files.notExists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        String message = "Unable to create file" + filePath;
        log.error(message);
        throw new RoomStorageException(message);
      }
    }
    writeRoomsToFile(filePath.toFile(), rooms);
  }

  List<Room> readRoomsFromFiles() {
    var folder = new File(folderName);
    if (!folder.exists() && !folder.mkdir()) {
      return List.of();
    }
    var files = folder.listFiles((d, name) -> isRoomFileForRead(name));
    if (null != files) {
      return Arrays.stream(files).flatMap(file -> readRoomsFromFile(file).stream()).toList();
    }
    return List.of();
  }

  private void writeRoomsToFile(final File file, final List<Room> rooms) {
    try (var writer = Files.newBufferedWriter(file.toPath())) {
      writer.write(Room.HEADERS + "\n");
      StatefulBeanToCsv<Room> csvWriter = new StatefulBeanToCsvBuilder<Room>(writer)
          .withOrderedResults(true)
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
          .withLineEnd(CSVWriter.DEFAULT_LINE_END)
          .build();

      csvWriter.write(rooms);

    } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
      var message = "Unable to write file " + file.getPath();
      log.error(message);
      throw new RoomStorageException(message);
    }
  }

  private boolean isRoomFileForRead(final String fileName) {
    return fileName.startsWith(roomFileStart + LocalDate.now().format(MONTH_FORMATTER))
        && fileName.endsWith(fileEnd);
  }

  private List<Room> readRoomsFromFile(final File file) {
    try {
      return new CsvToBeanBuilder<Room>(Files.newBufferedReader(file.toPath()))
          .withType(Room.class)
          .withSkipLines(1)
          .build().parse();
    } catch (IOException e) {
      var message = "Unable to parse .csv file " + file.toPath();
      log.error(message);
      throw new RoomStorageException(message);
    }
  }

  private List<Room> getAllByDate(final LocalDate date) {
    return getAll().stream()
        .filter(room -> date.equals(room.getUpdatedDate()))
        .toList();
  }

}
