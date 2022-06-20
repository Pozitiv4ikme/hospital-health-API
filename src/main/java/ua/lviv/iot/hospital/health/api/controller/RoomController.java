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
import ua.lviv.iot.hospital.health.api.model.dto.RoomDto;
import ua.lviv.iot.hospital.health.api.model.entity.Room;
import ua.lviv.iot.hospital.health.api.service.PatientService;
import ua.lviv.iot.hospital.health.api.service.RoomService;

@RestController
@RequestMapping("rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;
  private final PatientService patientService;

  @PostMapping
  public void create(@RequestBody Room room) {
    roomService.create(room);
  }

  @GetMapping("{id}")
  public Optional<RoomDto> getById(@PathVariable("id") long id) {
    return roomService.getById(id);
  }

  @PutMapping("{id}")
  public void update(@PathVariable("id") long id, @RequestBody Room room) {
    roomService.update(room, id);
  }

  @GetMapping
  public List<RoomDto> getAll() {
    return roomService.getAll();
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable("id") long id) {
    roomService.deleteById(id);
  }

  @GetMapping("{id}/patients")
  public void getAllById(@PathVariable("id") long roomId) {
    patientService.getAllByRoomId(roomId);
  }
}
