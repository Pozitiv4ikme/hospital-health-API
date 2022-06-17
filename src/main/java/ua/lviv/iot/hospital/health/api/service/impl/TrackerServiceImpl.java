package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.HealthCategory;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.repository.TrackerRepository;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@Service
@RequiredArgsConstructor
public class TrackerServiceImpl implements TrackerService {

  private final TrackerRepository trackerRepository;

  @Override
  public List<TrackerData> getTrackerDataByPatientId(int patientId) {
    return trackerRepository.getDataByPatientId(patientId);
  }

  @Override
  public void addTrackerData(int patientId, List<TrackerData> trackerDataList) {
    trackerRepository.saveDataForPatientId(patientId, trackerDataList);
  }

  @Override
  public HealthStatus getHealthStatus(List<TrackerData> trackerDataList) {
    return trackerDataList.stream()
        .map(this::getTrackerHealthStatus)
        .reduce(HealthStatus.UNKNOWN, this::reduceHealthStatus);
  }

  @Override
  public List<TrackerData> getTrackerDataById(int id) {
    return trackerRepository.getDataById(id);
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
}
