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
import ua.lviv.iot.hospital.health.api.model.dto.TrackerDto;
import ua.lviv.iot.hospital.health.api.model.entity.Tracker;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.service.TrackerService;

@RestController
@RequestMapping("trackers")
@RequiredArgsConstructor
public final class TrackerController {

  private final TrackerService trackerService;

  @PostMapping
  public void create(@RequestBody final Tracker tracker) {
    trackerService.create(tracker);
  }

  @PutMapping("{id}")
  public void update(@PathVariable("id") final long id, @RequestBody final Tracker tracker) {
    trackerService.update(id, tracker);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable("id") final long id) {
    trackerService.deleteById(id);
  }

  @GetMapping("{id}")
  public Optional<TrackerDto> getById(@PathVariable("id") final long id) {
    return trackerService.getById(id);
  }

  @GetMapping
  public List<TrackerDto> getAll() {
    return trackerService.getAll();
  }

  @GetMapping("{id}/data")
  public List<TrackerData> getTrackerDataById(@PathVariable("id") final long id) {
    return trackerService.getDataById(id);
  }

}
