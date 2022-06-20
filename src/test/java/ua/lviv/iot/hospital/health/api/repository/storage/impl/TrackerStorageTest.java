package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;

@SpringBootTest
public class TrackerStorageTest extends BaseStorageTest {

  @Autowired
  TrackerStorage trackerStorage;

  @Test
  void testWriteToFileAndLoadFromFile() {
    // given
    final var expectedTrackers = List.of(buildTracker(1), buildTracker(2), buildTracker(3));
    trackerStorage.writeTrackers(expectedTrackers, LocalDate.now());
    trackerStorage.loadFromFiles();

    // when
    final var actualTrackers = trackerStorage.getAll();

    // then
    assertThat(actualTrackers).containsExactlyInAnyOrderElementsOf(expectedTrackers);
  }

  @Test
  void testWriteToSeveralFilesAndLoadFromThem() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var trackersDay1 = List.of(buildTracker(1, day1),
        buildTracker(2, day1), buildTracker(3, day1));
    trackerStorage.writeTrackers(trackersDay1, day1);

    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var trackersDay2 = List.of(buildTracker(4, day2),
        buildTracker(5, day2), buildTracker(6, day2));
    trackerStorage.writeTrackers(trackersDay2, day2);

    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var trackersDay3 = List.of(buildTracker(7, day3),
        buildTracker(8, day3), buildTracker(9, day3));
    trackerStorage.writeTrackers(trackersDay3, day3);

    final var expectedTrackers = Stream.of(trackersDay1, trackersDay2, trackersDay3)
        .flatMap(Collection::stream)
        .toList();

    trackerStorage.loadFromFiles();

    // when
    final var actualTrackers = trackerStorage.getAll();

    // then
    assertThat(actualTrackers).containsExactlyInAnyOrderElementsOf(expectedTrackers);
  }

  @Test
  void testUpdatedDuplicatedInFiles() {
    // given
    final var day1 = LocalDate.now().withDayOfMonth(1);
    final var trackersDay1 = List.of(buildTracker(1, day1),
        buildTracker(2, day1), buildTracker(3, day1));
    trackerStorage.writeTrackers(trackersDay1, day1);

    // update tracker with id 2
    final var day2 = LocalDate.now().withDayOfMonth(2);
    final var trackersDay2 = List.of(buildTracker(4, day2),
        buildTracker(2, day2), buildTracker(5, day2));
    trackerStorage.writeTrackers(trackersDay2, day2);

    // update trackers with id 3 & 4
    final var day3 = LocalDate.now().withDayOfMonth(3);
    final var trackersDay3 = List.of(buildTracker(6, day3),
        buildTracker(3, day3), buildTracker(4, day3));
    trackerStorage.writeTrackers(trackersDay3, day3);

    // should load only the latest updated trackers
    final var expectedTrackers = List.of(trackersDay1.get(0),
        trackersDay2.get(1), trackersDay2.get(2),
        trackersDay3.get(0), trackersDay3.get(1), trackersDay3.get(2));

    trackerStorage.loadFromFiles();

    // when
    final var actualTrackers = trackerStorage.getAll();

    // then
    assertThat(actualTrackers).containsExactlyInAnyOrderElementsOf(expectedTrackers);
  }

  @Test
  void testCreateAndGetById() {
    // given
    final var tracker = buildTracker(1);

    // when
    trackerStorage.create(tracker);

    // then
    final var actualOptional = trackerStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isEqualTo(tracker);
  }

  @Test
  void testCreatedReadFromFile() {
    // given
    final var tracker = buildTracker(1);

    // when
    trackerStorage.create(tracker);
    trackerStorage.writeToFile();

    // then
    final var trackersFromFiles = trackerStorage.readTrackersFromFiles();
    assertThat(trackersFromFiles).containsOnly(tracker);
  }

  @Test
  void testUpdate() {
    // given
    final var origin = buildTracker(1);
    trackerStorage.writeTrackers(List.of(origin), LocalDate.now());
    trackerStorage.loadFromFiles();

    // when
    final var updated = buildTracker(1);
    updated.setModel(origin.getModel() + " Latest");
    trackerStorage.update(1, updated);

    // then
    final var actualOptional = trackerStorage.getById(1);
    assertThat(actualOptional).isNotEmpty();

    final var actual = actualOptional.get();
    assertThat(actual).isNotEqualTo(origin);
    assertThat(actual).isEqualTo(updated);
  }

  @Test
  void testUpdateReadFromFile() {
    // given
    final var origin = buildTracker(1);
    trackerStorage.writeTrackers(List.of(origin), LocalDate.now());
    trackerStorage.loadFromFiles();

    // when
    final var updated = buildTracker(1);
    updated.setModel(origin.getModel() + " NEW");
    trackerStorage.update(1, updated);
    trackerStorage.writeToFile();

    // then
    final var trackersFromFiles = trackerStorage.readTrackersFromFiles();
    assertThat(trackersFromFiles).containsOnly(updated);
  }

  @Test
  void testDelete() {
    // given
    final var origin = buildTracker(1);
    trackerStorage.writeTrackers(List.of(origin), LocalDate.now());
    trackerStorage.loadFromFiles();

    // when
    trackerStorage.deleteById(1);

    // then
    final var actualOptional = trackerStorage.getById(1);
    assertThat(actualOptional).isEmpty();
  }

  @Test
  void testDeletedReadFromFile() {
    // given
    final var origin = buildTracker(1);
    trackerStorage.writeTrackers(List.of(origin), LocalDate.now());
    trackerStorage.loadFromFiles();

    // when
    trackerStorage.deleteById(1);
    trackerStorage.writeToFile();

    // then
    final var trackersFromFiles = trackerStorage.readTrackersFromFiles();
    assertThat(trackersFromFiles).isEmpty();
  }

  private static Tracker buildTracker(final long id) {
    return buildTracker(id, LocalDate.now());
  }

  private static Tracker buildTracker(final long id, final LocalDate date) {
    final var tracker = new Tracker();
    tracker.setId(id);
    tracker.setModel("SuperModel" + id);
    tracker.setUpdatedDate(date);
    return tracker;
  }
}
