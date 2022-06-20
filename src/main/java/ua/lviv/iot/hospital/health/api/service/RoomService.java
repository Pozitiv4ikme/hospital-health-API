package ua.lviv.iot.hospital.health.api.service;

import java.util.List;
import java.util.Optional;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Room;

public interface RoomService extends BaseService<RoomDto, Room> {

  List<RoomDto> getAllByBuildingId(long buildingId);

}
