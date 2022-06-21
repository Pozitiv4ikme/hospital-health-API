package ua.lviv.iot.hospital.health.api.repository;

import java.util.List;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface TrackerDataRepository {

  List<TrackerData> getData();

  List<TrackerData> getDataByPatientId(long patientId);

  List<TrackerData> getDataById(long id);

  void saveDataForPatientId(long patientId, List<TrackerData> trackerDataList);
}
