package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Hospital extends Dated implements EntityId {

  public static final String HEADERS = "updatedDate,id,name";

  @CsvBindByPosition(position = 1)
  private long id;

  @CsvBindByPosition(position = 2)
  private String name;
}
