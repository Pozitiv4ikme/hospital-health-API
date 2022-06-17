package ua.lviv.iot.hospital.health.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ua.lviv.iot.hospital.health.api.model.TrackerDataType;
import ua.lviv.iot.hospital.health.api.model.converter.InstantConverter;

@Getter
@Setter
public class TrackerData {
  @CsvBindByPosition(position = 0)
  private int trackerId;

  @CsvBindByPosition(position = 1)
  private int patientId;

  @CsvBindByPosition(position = 2)
  private TrackerDataType type;

  @CsvBindAndSplitByPosition(position = 3, elementType = Float.class, splitOn = ";+", writeDelimiter = ";")
  private List<Float> values;

  @CsvCustomBindByPosition(position = 4, converter = InstantConverter.class)
  private Instant createdAt = Instant.now();
}
