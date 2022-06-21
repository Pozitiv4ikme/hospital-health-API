package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

@SpringBootTest
public class PatientStorageTest extends BaseStorageTest {

  @Autowired
  PatientStorage patientStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    final var expectedPatients = List.of(buildPatient(1), buildPatient(2), buildPatient(3));
    patientStorage.writePatients(expectedPatients, LocalDate.now());
    patientStorage.loadFromFiles();

    // when
    final var actualPatients = patientStorage.getAll();

    // then
    assertThat(actualPatients).containsExactlyInAnyOrderElementsOf(expectedPatients);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var patientsDay1 = List.of(buildPatient(1, day1),
        buildPatient(2, day1), buildPatient(3, day1));
    patientStorage.writePatients(patientsDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var patientsDay2 = List.of(buildPatient(4, day2),
        buildPatient(5, day2), buildPatient(6, day2));
    patientStorage.writePatients(patientsDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var patientsDay3 = List.of(buildPatient(7, day3),
        buildPatient(8, day3), buildPatient(9, day3));
    patientStorage.writePatients(patientsDay3, day3);

    final var expectedPatients = Stream.of(patientsDay1, patientsDay2, patientsDay3)
        .flatMap(Collection::stream)
        .toList();

    patientStorage.loadFromFiles();

    // when
    final var actualPatients = patientStorage.getAll();

    // then
    assertThat(actualPatients).containsExactlyInAnyOrderElementsOf(expectedPatients);
  }

  @Test
  void testUpdatedDuplicatedInFiles() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var patientsDay1 = List.of(buildPatient(1, day1),
        buildPatient(2, day1), buildPatient(3, day1));
    patientStorage.writePatients(patientsDay1, day1);

    // update patient with id 2
    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var patientsDay2 = List.of(buildPatient(4, day2),
        buildPatient(2, day2), buildPatient(5, day2));
    patientStorage.writePatients(patientsDay2, day2);

    // update patients with id 3 & 4
    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var patientsDay3 = List.of(buildPatient(6, day3),
        buildPatient(3, day3), buildPatient(4, day3));
    patientStorage.writePatients(patientsDay3, day3);

    // should load only the latest updated patients
    final var expectedPatients = List.of(patientsDay1.get(0),
        patientsDay2.get(1), patientsDay2.get(2),
        patientsDay3.get(0), patientsDay3.get(1), patientsDay3.get(2));

    patientStorage.loadFromFiles();

    // when
    final var actualPatients = patientStorage.getAll();

    // then
    assertThat(actualPatients).containsExactlyInAnyOrderElementsOf(expectedPatients);
  }

  @Test
  void testCreateAndGetById() {
    // given
    final var patient = buildPatient(1);

    // when
    patientStorage.create(patient);

    // then
    final var actualOptional = patientStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isEqualTo(patient);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    final var patient = buildPatient(1);

    // when
    patientStorage.create(patient);
    patientStorage.writeToFile();

    // then
    final var patientsFromFiles = patientStorage.readPatientsFromFiles();
    assertThat(patientsFromFiles).containsOnly(patient);
  }

  @Test
  void testUpdate() {
    // given
    final var origin = buildPatient(1);
    patientStorage.writePatients(List.of(origin), LocalDate.now());
    patientStorage.loadFromFiles();

    // when
    final var updated = buildPatient(1);
    updated.setName(origin.getName() + " New");
    patientStorage.update(1, updated);

    // then
    final var actualOptional = patientStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    final var origin = buildPatient(1);
    patientStorage.writePatients(List.of(origin), LocalDate.now());
    patientStorage.loadFromFiles();

    // when
    final var updated = buildPatient(1);
    updated.setSurname(origin.getSurname() + " NEW");
    patientStorage.update(1, updated);
    patientStorage.writeToFile();

    // then
    final var patientsFromFiles = patientStorage.readPatientsFromFiles();
    assertThat(patientsFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    final var origin = buildPatient(1);
    patientStorage.writePatients(List.of(origin), LocalDate.now());
    patientStorage.loadFromFiles();

    // when
    patientStorage.deleteById(1);

    // then
    final var actualOptional = patientStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    final var origin = buildPatient(1);
    patientStorage.writePatients(List.of(origin), LocalDate.now());
    patientStorage.loadFromFiles();

    // when
    patientStorage.deleteById(1);
    patientStorage.writeToFile();

    // then
    final var patientsFromFiles = patientStorage.readPatientsFromFiles();
    assertThat(patientsFromFiles).isEmpty();
  }

  private static Patient buildPatient(final long id) {
    return buildPatient(id, LocalDate.now());
  }

  private static Patient buildPatient(final long id, final LocalDate date) {
    final var patient = new Patient();
    patient.setId(id);
    patient.setName("test" + id);
    patient.setSurname("Test" + id);
    patient.setPhoneNumber("123456-00" + id);
    patient.setRoomId(2);
    patient.setUpdatedDate(date);
    return patient;
  }
}
