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
    final var expectedRooms = List.of(buildRoom(1), buildRoom(2), buildRoom(3));
    roomStorage.writeEntities(expectedRooms, LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    final var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsExactlyInAnyOrderElementsOf(expectedRooms);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var roomsDay1 = List.of(buildRoom(1, day1),
        buildRoom(2, day1), buildRoom(3, day1));
    roomStorage.writeEntities(roomsDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var roomsDay2 = List.of(buildRoom(4, day2),
        buildRoom(5, day2), buildRoom(6, day2));
    roomStorage.writeEntities(roomsDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var roomsDay3 = List.of(buildRoom(7, day3),
        buildRoom(8, day3), buildRoom(9, day3));
    roomStorage.writeEntities(roomsDay3, day3);

    final var expectedRooms = Stream.of(roomsDay1, roomsDay2, roomsDay3)
        .flatMap(Collection::stream)
        .toList();

    roomStorage.loadFromFiles();

    // when
    final var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsExactlyInAnyOrderElementsOf(expectedRooms);
  }

  @Test
  void testUpdatedDuplicatedInFiles() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var roomsDay1 = List.of(buildRoom(1, day1),
        buildRoom(2, day1), buildRoom(3, day1));
    roomStorage.writeEntities(roomsDay1, day1);

    // update room with id 2
    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var roomsDay2 = List.of(buildRoom(4, day2),
        buildRoom(2, day2), buildRoom(5, day2));
    roomStorage.writeEntities(roomsDay2, day2);

    // update rooms with id 3 & 4
    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var roomsDay3 = List.of(buildRoom(6, day3),
        buildRoom(3, day3), buildRoom(4, day3));
    roomStorage.writeEntities(roomsDay3, day3);

    // should load only the latest updated rooms
    final var expectedRooms = List.of(roomsDay1.get(0),
        roomsDay2.get(1), roomsDay2.get(2),
        roomsDay3.get(0), roomsDay3.get(1), roomsDay3.get(2));

    roomStorage.loadFromFiles();

    // when
    final var actualRooms = roomStorage.getAll();

    // then
    assertThat(actualRooms).containsExactlyInAnyOrderElementsOf(expectedRooms);
  }

  @Test
  void testCreateAndGetById() {
    // given
    final var room = buildRoom(1);

    // when
    roomStorage.create(room);

    // then
    final var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isEqualTo(room);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    final var room = buildRoom(1);

    // when
    roomStorage.create(room);
    roomStorage.writeToFile();

    // then
    final var roomsFromFiles = roomStorage.readEntitiesFromFiles();
    assertThat(roomsFromFiles).containsOnly(room);
  }

  @Test
  void testUpdate() {
    // given
    final var origin = buildRoom(1);
    roomStorage.writeEntities(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    final var updated = buildRoom(1);
    updated.setNumber(origin.getNumber() + 10);
    roomStorage.update(1, updated);

    // then
    final var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    final var origin = buildRoom(1);
    roomStorage.writeEntities(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    final var updated = buildRoom(1);
    updated.setNumber(origin.getNumber() + 20);
    roomStorage.update(1, updated);
    roomStorage.writeToFile();

    // then
    final var roomsFromFiles = roomStorage.readEntitiesFromFiles();
    assertThat(roomsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    final var origin = buildRoom(1);
    roomStorage.writeEntities(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    roomStorage.deleteById(1);

    // then
    final var actualOptional = roomStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    final var origin = buildRoom(1);
    roomStorage.writeEntities(List.of(origin), LocalDate.now());
    roomStorage.loadFromFiles();

    // when
    roomStorage.deleteById(1);
    roomStorage.writeToFile();

    // then
    final var roomsFromFiles = roomStorage.readEntitiesFromFiles();
    assertThat(roomsFromFiles).isEmpty();
  }

  private static Room buildRoom(final long id) {
    return buildRoom(id, LocalDate.now());
  }

  private static Room buildRoom(final long id, final LocalDate date) {
    final var room = new Room();
    room.setId(id);
    room.setNumber(101);
    room.setBuildingId(1);
    room.setUpdatedDate(date);
    return room;
  }
}
