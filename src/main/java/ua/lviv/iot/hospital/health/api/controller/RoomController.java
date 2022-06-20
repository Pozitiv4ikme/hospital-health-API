package ua.lviv.iot.hospital.health.api.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.dto.PatientDto;
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.RoomService;

@RestController
@RequestMapping("rooms")
@RequiredArgsConstructor
public final class RoomController {

  private final RoomService roomService;
  private final PatientService patientService;

  @PostMapping
  public void create(@RequestBody final Room room) {
    roomService.create(room);
  }

  @GetMapping("{id}")
  public Optional<RoomDto> getById(@PathVariable("id") final long id) {
    return roomService.getById(id);
  }

  @PutMapping("{id}")
  public void update(@PathVariable("id") final long id, @RequestBody final Room room) {
    roomService.update(id, room);
  }

  @GetMapping
  public List<RoomDto> getAll() {
    return roomService.getAll();
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable("id") final long id) {
    roomService.deleteById(id);
  }

  @GetMapping("{id}/patients")
  public List<PatientDto> getAllPatientsById(@PathVariable("id") final long roomId) {
    return patientService.getAllByRoomId(roomId);
  }
}
