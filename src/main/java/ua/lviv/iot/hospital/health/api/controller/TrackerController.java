package ua.lviv.iot.hospital.health.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.lviv.iot.hospital.health.api.model.entity.TrackerData;
import ua.lviv.iot.hospital.health.api.service.impl.TrackerServiceImpl;

@RestController
@RequestMapping("trackers")
@RequiredArgsConstructor
public class TrackerController {
  private final TrackerServiceImpl trackerService;

  @GetMapping("{id}")
  public List<TrackerData> getTrackerDataById(@PathVariable ("id") int id){
    return trackerService.getTrackerDataById(id);
  }

}
