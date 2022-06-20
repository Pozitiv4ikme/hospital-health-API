package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.entity.Room;

@SpringBootTest
public class RoomStorageTest extends BaseStorageTest {

  @Autowired
  RoomStorage roomStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    var expectedRooms = List.of(buildRoom(1), buildRoom(2), buildRoom(3));
    roomStorage.writeRooms(expectedRooms, LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsAll(expectedRooms);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    var day1 = LocalDate.now().withDayOfMonth(1);
    var roomsDay1 = List.of(buildRoom(1, day1),
        buildRoom(2, day1), buildRoom(3, day1));
    roomStorage.writeRooms(roomsDay1, day1);

    var day2 = LocalDate.now().withDayOfMonth(2);
    var roomsDay2 = List.of(buildRoom(4, day2),
        buildRoom(5, day2), buildRoom(6, day2));
    roomStorage.writeRooms(roomsDay2, day2);

    var day3 = LocalDate.now().withDayOfMonth(3);
    var roomsDay3 = List.of(buildRoom(7, day3),
        buildRoom(8, day3), buildRoom(9, day3));
    roomStorage.writeRooms(roomsDay3, day3);

    var expectedRooms = Stream.of(roomsDay1, roomsDay2, roomsDay3)
        .flatMap(Collection::stream)
        .toList();

    roomStorage.loadFromFiles();

    // when
    var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsAll(expectedRooms);
  }

  @Test
  void testUpdatedDuplicatedInFiles() {
    // given
    var day1 = LocalDate.now().withDayOfMonth(1);
    var roomsDay1 = List.of(buildRoom(1, day1),
        buildRoom(2, day1), buildRoom(3, day1));
    roomStorage.writeRooms(roomsDay1, day1);

    // update room with id 2
    var day2 = LocalDate.now().withDayOfMonth(2);
    var roomsDay2 = List.of(buildRoom(4, day2),
        buildRoom(2, day2), buildRoom(5, day2));
    roomStorage.writeRooms(roomsDay2, day2);

    // update rooms with id 3 & 4
    var day3 = LocalDate.now().withDayOfMonth(3);
    var roomsDay3 = List.of(buildRoom(6, day3),
        buildRoom(3, day3), buildRoom(4, day3));
    roomStorage.writeRooms(roomsDay3, day3);

    // should load only the latest updated rooms
    var expectedRooms = List.of(roomsDay1.get(0), roomsDay1.get(2),
        roomsDay2.get(1), roomsDay2.get(2),
        roomsDay3.get(0), roomsDay3.get(1), roomsDay3.get(2));

    roomStorage.loadFromFiles();

    // when
    var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsAll(expectedRooms);
  }

  @Test
  void testCreateAndGetById() {
    // given
    var room = buildRoom(1);

    // when
    roomStorage.create(room);

    // then
    var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    var actual = actualOptional.get();
    assertThat(actual).isEqualTo(room);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    var room = buildRoom(1);

    // when
    roomStorage.create(room);
    roomStorage.writeToFile();

    // then
    var roomsFromFiles = roomStorage.readRoomsFromFiles();
    assertThat(roomsFromFiles).containsOnly(room);
  }

  @Test
  void testUpdate() {
    // given
    var origin = buildRoom(1);
    roomStorage.writeRooms(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    var updated = buildRoom(1);
    updated.setNumber(origin.getNumber() + 10);
    roomStorage.update(updated, 1);

    // then
    var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    var origin = buildRoom(1);
    roomStorage.writeRooms(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    var updated = buildRoom(1);
    updated.setNumber(origin.getNumber() + 20);
    roomStorage.update(updated, 1);
    roomStorage.writeToFile();

    // then
    var roomsFromFiles = roomStorage.readRoomsFromFiles();
    assertThat(roomsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    var origin = buildRoom(1);
    roomStorage.writeRooms(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    roomStorage.deleteById(1);

    // then
    var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    var origin = buildRoom(1);
    roomStorage.writeRooms(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    roomStorage.deleteById(1);
    roomStorage.writeToFile();

    // then
    var roomsFromFiles = roomStorage.readRoomsFromFiles();
    assertThat(roomsFromFiles).isEmpty();
  }

  private static Room buildRoom(long id) {
    return buildRoom(id, LocalDate.now());
  }

  private static Room buildRoom(long id, LocalDate date) {
    var room = new Room();
    room.setId(id);
    room.setNumber(101);
    room.setBuildingId(1);
    room.setUpdatedDate(date);
    return room;
  }
}
