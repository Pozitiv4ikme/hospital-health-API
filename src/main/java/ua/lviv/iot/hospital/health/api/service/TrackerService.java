package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface TrackerService {
   List<TrackerData> getTrackerDataByPatientId(int patientId);
   void addTrackerData(int patientId, List<TrackerData> trackerDataList) throws Exception;
   HealthStatus getHealthStatus(List<TrackerData> trackerDataList);
   List<TrackerData> getTrackerDataById(int id);
}
