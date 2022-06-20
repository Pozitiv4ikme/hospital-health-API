package ua.lviv.iot.hospital.health.api.service;


import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.HealthStatus;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

public interface PatientService extends BaseService<PatientDto, Patient> {

  List<PatientDto> getAllByRoomId(long roomId);

  HealthStatus getStatusById(long id);

}
