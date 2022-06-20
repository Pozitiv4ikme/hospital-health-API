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
    var expectedHospitals = List.of(buildHospital(1), buildHospital(2), buildHospital(3));
    hospitalStorage.writeHospitals(expectedHospitals, LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsAll(expectedHospitals);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    var day1 = LocalDate.now().withDayOfMonth(1);
    var hospitalsDay1 = List.of(buildHospital(1, day1),
        buildHospital(2, day1), buildHospital(3, day1));
    hospitalStorage.writeHospitals(hospitalsDay1, day1);

    var day2 = LocalDate.now().withDayOfMonth(2);
    var hospitalsDay2 = List.of(buildHospital(4, day2),
        buildHospital(5, day2), buildHospital(6, day2));
    hospitalStorage.writeHospitals(hospitalsDay2, day2);

    var day3 = LocalDate.now().withDayOfMonth(3);
    var hospitalsDay3 = List.of(buildHospital(7, day3),
        buildHospital(8, day3), buildHospital(9, day3));
    hospitalStorage.writeHospitals(hospitalsDay3, day3);

    var expectedHospitals = Stream.of(hospitalsDay1, hospitalsDay2, hospitalsDay3)
        .flatMap(Collection::stream)
        .toList();

    hospitalStorage.loadFromFiles();

    // when
    var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsAll(expectedHospitals);
  }

  @Test
  void testUpdatedDuplicatedHospitalsInFiles() {
    // given
    var day1 = LocalDate.now().withDayOfMonth(1);
    var hospitalsDay1 = List.of(buildHospital(1, day1),
        buildHospital(2, day1), buildHospital(3, day1));
    hospitalStorage.writeHospitals(hospitalsDay1, day1);

    // update hospital with id 2
    var day2 = LocalDate.now().withDayOfMonth(2);
    var hospitalsDay2 = List.of(buildHospital(4, day2),
        buildHospital(2, day2), buildHospital(5, day2));
    hospitalStorage.writeHospitals(hospitalsDay2, day2);

    // update hospitals with id 3 & 4
    var day3 = LocalDate.now().withDayOfMonth(3);
    var hospitalsDay3 = List.of(buildHospital(6, day3),
        buildHospital(3, day3), buildHospital(4, day3));
    hospitalStorage.writeHospitals(hospitalsDay3, day3);

    // should load only the latest updated hospitals
    var expectedHospitals = List.of(hospitalsDay1.get(0), hospitalsDay1.get(2),
        hospitalsDay2.get(1), hospitalsDay2.get(2),
        hospitalsDay3.get(0), hospitalsDay3.get(1), hospitalsDay3.get(2));

    hospitalStorage.loadFromFiles();

    // when
    var actualHospitals = hospitalStorage.getAll();

    // then
    assertThat(actualHospitals).containsAll(expectedHospitals);
  }

  @Test
  void testCreateAndGetById() {
    // given
    var hospital = buildHospital(1);

    // when
    hospitalStorage.create(hospital);

    // then
    var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    var actual = actualOptional.get();
    assertThat(actual).isEqualTo(hospital);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    var hospital = buildHospital(1);

    // when
    hospitalStorage.create(hospital);
    hospitalStorage.writeToFile();

    // then
    var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).containsOnly(hospital);
  }

  @Test
  void testUpdate() {
    // given
    var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    var updated = buildHospital(1);
    updated.setName("Updated name");
    hospitalStorage.update(updated, 1);

    // then
    var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    var updated = buildHospital(1);
    updated.setName("Updated name");
    hospitalStorage.update(updated, 1);
    hospitalStorage.writeToFile();

    // then
    var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    hospitalStorage.deleteById(1);

    // then
    var actualOptional = hospitalStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    var origin = buildHospital(1);
    hospitalStorage.writeHospitals(List.of(origin), LocalDate.now());
    hospitalStorage.loadFromFiles();

    // when
    hospitalStorage.deleteById(1);
    hospitalStorage.writeToFile();

    // then
    var hospitalsFromFiles = hospitalStorage.readHospitalsFromFiles();
    assertThat(hospitalsFromFiles).isEmpty();
  }

  private static Hospital buildHospital(long id) {
    return buildHospital(id, LocalDate.now());
  }

  private static Hospital buildHospital(long id, LocalDate date) {
    var hospital = new Hospital();
    hospital.setId(id);
    hospital.setName("test" + id);
    hospital.setUpdatedDate(date);
    return hospital;
  }
}
