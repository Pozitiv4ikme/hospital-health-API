package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.TrackerDto;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;

public interface TrackerService {

  Optional<TrackerDto> getById(long id);

  List<TrackerDto> getAll();

  List<TrackerDto> getAllByPatientId(long patientId);

  void create(Tracker tracker);

  void update(long id, Tracker tracker);

  void deleteById(long id);

  List<TrackerData> getDataById(long id);

  List<TrackerData> getDataByPatientId(long patientId);

  void addData(long patientId, List<TrackerData> trackerDataList) throws Exception;

  HealthStatus getHealthStatus(List<TrackerData> trackerDataList);
}
