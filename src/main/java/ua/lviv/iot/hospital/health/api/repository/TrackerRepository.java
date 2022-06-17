package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface TrackerRepository {
  List<TrackerData> getData();
  List<TrackerData> getDataByPatientId(int patientId);
  List<TrackerData> getDataById(int id);
  void saveDataForPatientId(int patientId, List<TrackerData> trackerDataList);
}
