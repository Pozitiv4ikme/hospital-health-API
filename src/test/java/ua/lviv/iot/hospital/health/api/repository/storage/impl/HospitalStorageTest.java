package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.entity.Hospital;

@SpringBootTest
public class HospitalStorageTest extends BaseStorageTest {

  @Autowired
  HospitalStorage hospitalStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    final var expectedHospitals = List.of(buildHospital(1), buildHospital(2), buildHospital(3));
    hospitalStorage.writeHospitals(expectedHospitals, LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    final var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsExactlyInAnyOrderElementsOf(expectedHospitals);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var hospitalsDay1 = List.of(buildHospital(1, day1),
        buildHospital(2, day1), buildHospital(3, day1));
    hospitalStorage.writeHospitals(hospitalsDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var hospitalsDay2 = List.of(buildHospital(4, day2),
        buildHospital(5, day2), buildHospital(6, day2));
    hospitalStorage.writeHospitals(hospitalsDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var hospitalsDay3 = List.of(buildHospital(7, day3),
        buildHospital(8, day3), buildHospital(9, day3));
    hospitalStorage.writeHospitals(hospitalsDay3, day3);

    final var expectedHospitals = Stream.of(hospitalsDay1, hospitalsDay2, hospitalsDay3)
        .flatMap(Collection::stream)
        .toList();

    hospitalStorage.loadFromFiles();

    // when
    final var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsExactlyInAnyOrderElementsOf(expectedHospitals);
  }

  @Test
  void testUpdatedDuplicatedHospitalsInFiles() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var hospitalsDay1 = List.of(buildHospital(1, day1),
        buildHospital(2, day1), buildHospital(3, day1));
    hospitalStorage.writeHospitals(hospitalsDay1, day1);

    // update hospital with id 2
    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var hospitalsDay2 = List.of(buildHospital(4, day2),
        buildHospital(2, day2), buildHospital(5, day2));
    hospitalStorage.writeHospitals(hospitalsDay2, day2);

    // update hospitals with id 3 & 4
    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var hospitalsDay3 = List.of(buildHospital(6, day3),
        buildHospital(3, day3), buildHospital(4, day3));
    hospitalStorage.writeHospitals(hospitalsDay3, day3);

    // should load only the latest updated hospitals
    final var expectedHospitals = List.of(hospitalsDay1.get(0),
        hospitalsDay2.get(1), hospitalsDay2.get(2),
        hospitalsDay3.get(0), hospitalsDay3.get(1), hospitalsDay3.get(2));

    hospitalStorage.loadFromFiles();

    // when
    final var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsExactlyInAnyOrderElementsOf(expectedHospitals);
  }

  @Test
  void testCreateAndGetById() {
    // given
    final var hospital = buildHospital(1);

    // when
    hospitalStorage.create(hospital);

    // then
    final var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isEqualTo(hospital);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    final var hospital = buildHospital(1);

    // when
    hospitalStorage.create(hospital);
    hospitalStorage.writeToFile();

    // then
    final var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).containsOnly(hospital);
  }

  @Test
  void testUpdate() {
    // given
    final var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    final var updated = buildHospital(1);
    updated.setName("Updated name");
    hospitalStorage.update(updated, 1);

    // then
    final var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    final var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    final var updated = buildHospital(1);
    updated.setName("Updated name");
    hospitalStorage.update(updated, 1);
    hospitalStorage.writeToFile();

    // then
    final var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    final var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    hospitalStorage.deleteById(1);

    // then
    final var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    final var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    hospitalStorage.deleteById(1);
    hospitalStorage.writeToFile();

    // then
    final var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).isEmpty();
  }

  private static Hospital buildHospital(final long id) {
    return buildHospital(id, LocalDate.now());
  }

  private static Hospital buildHospital(final long id, final LocalDate date) {
    final var hospital = new Hospital();
    hospital.setId(id);
    hospital.setName("test" + id);
    hospital.setUpdatedDate(date);
    return hospital;
  }
}
