package ua.lviv.iot.hospital.health.api.repository.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.repository.TrackerDataRepository;
import ua.lviv.iot.hospital.health.api.repository.storage.impl.TrackerDataStorage;

@Repository
@RequiredArgsConstructor
public final class TrackerDataRepositoryImpl implements TrackerDataRepository {

  private final TrackerDataStorage trackerDataStorage;

  @Override
  public List<TrackerData> getData() {
    return trackerDataStorage.getDataAll();
  }

  @Override
  public List<TrackerData> getDataByPatientId(final long patientId) {
    return trackerDataStorage.getDataByPatientId(patientId);
  }

  @Override
  public List<TrackerData> getDataById(final long id) {
    return trackerDataStorage.getDataByTrackerId(id);
  }

  @Override
  public void saveDataForPatientId(final long patientId, final List<TrackerData> trackerDataList) {
    trackerDataStorage.saveData(patientId, trackerDataList);
  }
}
