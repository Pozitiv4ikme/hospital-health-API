package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.entity.Building;

@SpringBootTest
public class BuildingStorageTest extends BaseStorageTest {

  @Autowired
  BuildingStorage buildingStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    final var expectedBuildings = List.of(buildBuilding(1), buildBuilding(2), buildBuilding(3));
    buildingStorage.writeBuildings(expectedBuildings, LocalDate.now());
    buildingStorage.loadFromFiles();

    // when
    final var actualBuildings = buildingStorage.getAll();

    // then
    assertThat(actualBuildings).containsExactlyInAnyOrderElementsOf(expectedBuildings);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var buildingsDay1 = List.of(buildBuilding(1, day1),
        buildBuilding(2, day1), buildBuilding(3, day1));
    buildingStorage.writeBuildings(buildingsDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var buildingsDay2 = List.of(buildBuilding(4, day2),
        buildBuilding(5, day2), buildBuilding(6, day2));
    buildingStorage.writeBuildings(buildingsDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var buildingsDay3 = List.of(buildBuilding(7, day3),
        buildBuilding(8, day3), buildBuilding(9, day3));
    buildingStorage.writeBuildings(buildingsDay3, day3);

    final var expectedBuildings = Stream.of(buildingsDay1, buildingsDay2, buildingsDay3)
        .flatMap(Collection::stream)
        .toList();

    buildingStorage.loadFromFiles();

    // when
    final var actualBuildings = buildingStorage.getAll();

    // then
    assertThat(actualBuildings).containsExactlyInAnyOrderElementsOf(expectedBuildings);
  }

  @Test
  void testUpdatedDuplicatedInFiles() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var buildingsDay1 = List.of(buildBuilding(1, day1),
        buildBuilding(2, day1), buildBuilding(3, day1));
    buildingStorage.writeBuildings(buildingsDay1, day1);

    // update building with id 2
    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var buildingsDay2 = List.of(buildBuilding(4, day2),
        buildBuilding(2, day2), buildBuilding(5, day2));
    buildingStorage.writeBuildings(buildingsDay2, day2);

    // update buildings with id 3 & 4
    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var buildingsDay3 = List.of(buildBuilding(6, day3),
        buildBuilding(3, day3), buildBuilding(4, day3));
    buildingStorage.writeBuildings(buildingsDay3, day3);

    // should load only the latest updated building
    final var expectedBuildings = List.of(buildingsDay1.get(0),
        buildingsDay2.get(1), buildingsDay2.get(2),
        buildingsDay3.get(0), buildingsDay3.get(1), buildingsDay3.get(2));

    buildingStorage.loadFromFiles();

    // when
    final var actualBuildings = buildingStorage.getAll();

    // then
    assertThat(actualBuildings).containsExactlyInAnyOrderElementsOf(expectedBuildings);
  }

  @Test
  void testCreateAndGetById() {
    // given
    final var building = buildBuilding(1);

    // when
    buildingStorage.create(building);

    // then
    final var actualOptional = buildingStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isEqualTo(building);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    final var building = buildBuilding(1);

    // when
    buildingStorage.create(building);
    buildingStorage.writeToFile();

    // then
    final var buildingsFromFiles = buildingStorage.readBuildingsFromFiles();
    assertThat(buildingsFromFiles).containsOnly(building);
  }

  @Test
  void testUpdate() {
    // given
    final var origin = buildBuilding(1);
    buildingStorage.writeBuildings(List.of(origin), LocalDate.now());
    buildingStorage.loadFromFiles();

    // when
    final var updated = buildBuilding(1);
    updated.setName("Updated name");
    buildingStorage.update(updated, 1);

    // then
    final var actualOptional = buildingStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    final var origin = buildBuilding(1);
    buildingStorage.writeBuildings(List.of(origin), LocalDate.now());
    buildingStorage.loadFromFiles();

    // when
    final var updated = buildBuilding(1);
    updated.setName("Updated name");
    buildingStorage.update(updated, 1);
    buildingStorage.writeToFile();

    // then
    final var buildingsFromFiles = buildingStorage.readBuildingsFromFiles();
    assertThat(buildingsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    final var origin = buildBuilding(1);
    buildingStorage.writeBuildings(List.of(origin), LocalDate.now());
    buildingStorage.loadFromFiles();

    // when
    buildingStorage.deleteById(1);

    // then
    final var actualOptional = buildingStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    final var origin = buildBuilding(1);
    buildingStorage.writeBuildings(List.of(origin), LocalDate.now());
    buildingStorage.loadFromFiles();

    // when
    buildingStorage.deleteById(1);
    buildingStorage.writeToFile();

    // then
    final var buildingsFromFiles = buildingStorage.readBuildingsFromFiles();
    assertThat(buildingsFromFiles).isEmpty();
  }

  private static Building buildBuilding(final long id) {
    return buildBuilding(id, LocalDate.now());
  }

  private static Building buildBuilding(final long id, final LocalDate date) {
    final var building = new Building();
    building.setId(id);
    building.setName("test" + id);
    building.setAddress("address" + id);
    building.setHospitalId(1);
    building.setUpdatedDate(date);
    return building;
  }
}
