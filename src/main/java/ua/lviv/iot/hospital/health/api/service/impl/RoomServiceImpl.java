package ua.lviv.iot.hospital.health.api.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.repository.RoomRepository;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.RoomService;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
  private final RoomRepository roomRepository;
  private final PatientService patientService;

  @Override
  public Optional<RoomDto> getById(long id) {
    return roomRepository.getById(id)
        .map(room -> buildRoomDto(room, patientService.getAllByRoomId(id)));
  }

  @Override
  public List<RoomDto> getAll() {
    return roomRepository.getAll().stream()
        .map(room -> buildRoomDto(room, patientService.getAllByRoomId(room.getId())))
        .toList();
  }

  @Override
  public List<RoomDto> getAllByBuildingId(long buildingId) {
    return getAll().stream()
        .filter(roomDto -> roomDto.buildingId() == buildingId)
        .toList();
  }

  @Override
  public void create(Room room) {
    roomRepository.create(room);
  }

  @Override
  public void update(Room room, long id) {
    roomRepository.update(room, id);
  }

  @Override
  public void deleteById(long id) {
    roomRepository.deleteById(id);
  }

  private RoomDto buildRoomDto(Room room, List<PatientDto> patients) {
    return RoomDto.builder()
        .id(room.getId())
        .number(room.getNumber())
        .buildingId(room.getBuildingId())
        .patients(patients)
        .build();
  }
}
