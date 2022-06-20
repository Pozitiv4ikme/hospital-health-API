package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.exception.tracker.TrackerServiceException;
import ua.lviv.iot.hospital.health.api.model.HealthCategory;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.TrackerDto;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.repository.TrackerDataRepository;
import ua.lviv.iot.hospital.health.api.repository.TrackerRepository;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackerServiceImpl implements TrackerService {

  private final TrackerRepository trackerRepository;
  private final TrackerDataRepository trackerDataRepository;

  @Override
  public Optional<TrackerDto> getById(long id) {
    return trackerRepository.getById(id)
        .map(tracker -> buildTrackerDto(tracker, getDataById(id)));
  }

  @Override
  public List<TrackerDto> getAll() {
    return trackerRepository.getAll().stream()
        .map(tracker -> buildTrackerDto(tracker, getDataById(tracker.getId())))
        .toList();
  }

  @Override
  public void create(Tracker tracker) {
    trackerRepository.create(tracker);
  }

  @Override
  public void update(long id, Tracker tracker) {
    trackerRepository.update(id, tracker);
  }

  @Override
  public void deleteById(long id) {
    trackerRepository.deleteById(id);
  }

  @Override
  public List<TrackerDto> getAllByPatientId(long patientId) {
    var trackerDataMap = getDataByPatientId(patientId).stream()
        .collect(Collectors.groupingBy(TrackerData::getTrackerId));
    return trackerDataMap.keySet().stream()
        .map(trackerId -> trackerRepository.getById(trackerId)
            .map(tracker -> buildTrackerDto(tracker, trackerDataMap.get(trackerId))))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  @Override
  public List<TrackerData> getDataById(long id) {
    return trackerDataRepository.getDataById(id);
  }

  @Override
  public List<TrackerData> getDataByPatientId(long patientId) {
    return trackerDataRepository.getDataByPatientId(patientId);
  }

  @Override
  public void addData(long patientId, List<TrackerData> trackerDataList) {
    trackerDataList.forEach(trackerData -> {
      if (trackerData.getPatientId() !=0  && patientId != trackerData.getPatientId()) {
        String message = "trackerData.patientId is not empty and does not match patientId";
        log.error(message);
        throw new TrackerServiceException(message);
      }
    });
    trackerDataRepository.saveDataForPatientId(patientId, trackerDataList);
  }

  @Override
  public HealthStatus getHealthStatus(List<TrackerData> trackerDataList) {
    return trackerDataList.stream()
        .map(this::getTrackerHealthStatus)
        .reduce(HealthStatus.UNKNOWN, this::reduceHealthStatus);
  }

  private HealthStatus getTrackerHealthStatus(TrackerData trackerData) {
    return Arrays.stream(HealthCategory.values())
        .filter(category -> hasTrackerDataInRanges(category, trackerData))
        .map(HealthCategory::getStatus)
        .reduce(HealthStatus.UNKNOWN, this::reduceHealthStatus);
  }

  private HealthStatus reduceHealthStatus(HealthStatus s1, HealthStatus s2) {
    return s1.getPriority() > s2.getPriority() ? s1 : s2;
  }

  private boolean hasTrackerDataInRanges(HealthCategory category, TrackerData trackerData) {
    var values = trackerData.getValues();
    var ranges = category.getTypeRangeMap().get(trackerData.getType());
    boolean isInRange = false;
    int amount = trackerData.getType().getValueAmount();
    for (int i = 0; i < amount; i++) {
      isInRange = isInRange || (values.size() >= i + 1 && ranges.size() >= i + 1
          && values.get(i) >= ranges.get(i).min()
          && values.get(i) <= ranges.get(i).max());
    }
    return isInRange;
  }

  private TrackerDto buildTrackerDto(Tracker tracker, List<TrackerData> trackerData) {
    return TrackerDto.builder()
        .id(tracker.getId())
        .model(tracker.getModel())
        .healthStatus(getHealthStatus(trackerData))
        .trackerData(trackerData)
        .build();
  }
}
