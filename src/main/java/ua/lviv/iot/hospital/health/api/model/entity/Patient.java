package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Patient {
  public static final String HEADERS = "id,name,surname,roomId,phoneNumber";

  @CsvBindByPosition(position = 0)
  private long id;

  @CsvBindByPosition(position = 1)
  private String name;

  @CsvBindByPosition(position = 2)
  private String surname;

  @CsvBindByPosition(position = 3)
  private long roomId;

  @CsvBindByPosition(position = 4)
  private String phoneNumber;

}
