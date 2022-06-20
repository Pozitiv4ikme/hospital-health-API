package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Room extends Dated {

  public static final String HEADERS = "updatedDate,id,number,buildingId";

  @CsvBindByPosition(position = 1)
  private long id;

  @CsvBindByPosition(position = 2)
  private int number;

  @CsvBindByPosition(position = 3)
  private long buildingId;
}
