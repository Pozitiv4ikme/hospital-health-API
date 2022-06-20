package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import ua.lviv.iot.hospital.health.api.model.dto.BuildingDto;
import ua.lviv.iot.hospital.health.api.model.entity.Building;

public interface BuildingService extends BaseService<BuildingDto, Building> {

  List<BuildingDto> getAllByHospitalId(long hospitalId);

}
