package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Patient extends Dated {

  public static final String HEADERS = "updatedDate,id,name,surname,roomId,phoneNumber";

  @CsvBindByPosition(position = 1)
  private long id;

  @CsvBindByPosition(position = 2)
  private String name;

  @CsvBindByPosition(position = 3)
  private String surname;

  @CsvBindByPosition(position = 4)
  private long roomId;

  @CsvBindByPosition(position = 5)
  private String phoneNumber;

}
