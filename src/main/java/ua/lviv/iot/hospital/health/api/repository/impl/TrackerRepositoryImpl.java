package ua.lviv.iot.hospital.health.api.repository.impl;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.repository.TrackerRepository;
import ua.lviv.iot.hospital.health.api.storage.TrackerStorage;

@Repository
@RequiredArgsConstructor
public class TrackerRepositoryImpl implements TrackerRepository {

  private final TrackerStorage trackerStorage;

  @Override
  public List<TrackerData> getData() {
    return trackerStorage.getDataAll();
  }

  @Override
  public List<TrackerData> getDataByPatientId(int patientId) {
    return trackerStorage.getDataByPatientId(patientId);
  }

  @Override
  public List<TrackerData> getDataById(int id) {
    return trackerStorage.getDataById(id);
  }

  @Override
  public void saveDataForPatientId(int patientId, List<TrackerData> trackerDataList) {
    trackerDataList.forEach(trackerData -> trackerData.setPatientId(patientId));
    trackerStorage.saveData(trackerDataList);
  }
}
