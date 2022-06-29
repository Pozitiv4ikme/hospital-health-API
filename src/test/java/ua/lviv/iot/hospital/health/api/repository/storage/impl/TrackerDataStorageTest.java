package ua.lviv.iot.hospital.health.api.repository.storage.impl;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.TrackerDataType;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

@SpringBootTest
public class TrackerDataStorageTest extends BaseStorageTest {

  @Autowired
  TrackerDataStorage trackerDataStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    final var expectedTrackerData = List.of(buildTrackerData(1), buildTrackerData(2), buildTrackerData(3));
    trackerDataStorage.writeEntitiesToFile(expectedTrackerData, LocalDate.now());
    trackerDataStorage.loadFromFiles();

    // when
    final var actualTrackerData = trackerDataStorage.getAll();

    // then
    assertThat(actualTrackerData).containsExactlyInAnyOrderElementsOf(expectedTrackerData);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var trackerDataDay1 = List.of(buildTrackerData(1, 1),
        buildTrackerData(2, 1), buildTrackerData(3, 2));
    trackerDataStorage.writeEntitiesToFile(trackerDataDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var trackerDataDay2 = List.of(buildTrackerData(4, 2),
        buildTrackerData(5, 2), buildTrackerData(6, 3));
    trackerDataStorage.writeEntitiesToFile(trackerDataDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var trackerDataDay3 = List.of(buildTrackerData(7, 4),
        buildTrackerData(8, 5), buildTrackerData(9, 6));
    trackerDataStorage.writeEntitiesToFile(trackerDataDay3, day3);

    final var expectedTrackerData = Stream.of(trackerDataDay1, trackerDataDay2, trackerDataDay3)
        .flatMap(Collection::stream)
        .toList();

    trackerDataStorage.loadFromFiles();

    // when
    final var actualTrackerData = trackerDataStorage.getAll();

    // then
    assertThat(actualTrackerData).containsExactlyInAnyOrderElementsOf(expectedTrackerData);
  }

  @Test
  void testSaveAndGetByTrackerId() {
    // given
    final var trackerData = buildTrackerData(1, 1);

    // when
    trackerDataStorage.save(1, List.of(trackerData));

    // then
    final var actualTrackerData = trackerDataStorage.getByTrackerId(1);
    assertThat(actualTrackerData).isNotEmpty();

    assertThat(actualTrackerData).containsOnly(trackerData);
  }

  @Test
  void testSaveAndGetByPatientId() {
    // given
    final var trackerData1 = buildTrackerData(1, 1);
    final var trackerData2 = buildTrackerData(2, 1);
    final var trackerData3 = buildTrackerData(3, 2);
    final var expectedTrackerData = List.of(trackerData1, trackerData2);

    // when
    trackerDataStorage.save(trackerData1.getPatientId(), List.of(trackerData1));
    trackerDataStorage.save(trackerData2.getPatientId(), List.of(trackerData2));
    trackerDataStorage.save(trackerData3.getPatientId(), List.of(trackerData3));

    // then
    final var actualTrackerData = trackerDataStorage.getByPatientId(1);
    assertThat(actualTrackerData).isNotEmpty();

    assertThat(actualTrackerData).containsExactlyInAnyOrderElementsOf(expectedTrackerData);
  }

  @Test
  void testCreatedReadFromFileWithoutExplicitWriteToFile() {
    // given
    final var trackerData = buildTrackerData(1, 3);

    // when
    trackerDataStorage.save(3, List.of(trackerData));

    // then
    final var trackerDataFromFiles = trackerDataStorage.readEntitiesFromFiles();
    assertThat(trackerDataFromFiles).containsOnly(trackerData);
  }

  private static TrackerData buildTrackerData(final long trackerId) {
    return buildTrackerData(trackerId, 1L);
  }

  private static TrackerData buildTrackerData(final long trackerId, final long patientId) {
    final var trackerData = new TrackerData();
    trackerData.setTrackerId(trackerId);
    trackerData.setPatientId(patientId);
    trackerData.setType(TrackerDataType.PRESSURE);
    trackerData.setValues(List.of(1.0f, 2.0f, 3.0f));
    return trackerData;
  }
}
