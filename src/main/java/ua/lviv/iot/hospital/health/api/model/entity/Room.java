package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
  public static final String HEADERS = "id,number,buildingId";

  @CsvBindByPosition(position = 0)
  private long id;

  @CsvBindByPosition(position = 1)
  private int number;

  @CsvBindByPosition(position = 2)
  private long buildingId;
}
