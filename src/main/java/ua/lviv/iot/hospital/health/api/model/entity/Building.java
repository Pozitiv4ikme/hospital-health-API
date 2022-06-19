package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Building {

  public static final String HEADERS = "id,name,address,hospitalId";

  @CsvBindByPosition(position = 0)
  private long id;

  @CsvBindByPosition(position = 1)
  private String name;

  @CsvBindByPosition(position = 2)
  private String address;

  @CsvBindByPosition(position = 3)
  private long hospitalId;
}
