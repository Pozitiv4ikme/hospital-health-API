package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.TrackerDto;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface TrackerService extends BaseService<TrackerDto, Tracker> {

  List<TrackerDto> getAllByPatientId(long patientId);

  List<TrackerData> getDataById(long id);

  List<TrackerData> getDataByPatientId(long patientId);

  void addData(long patientId, List<TrackerData> trackerDataList) throws Exception;

  HealthStatus getHealthStatus(List<TrackerData> trackerDataList);
}
