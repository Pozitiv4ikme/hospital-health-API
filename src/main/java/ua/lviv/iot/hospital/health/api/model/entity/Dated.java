package ua.lviv.iot.hospital.health.api.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dated {

   @CsvBindByPosition(position = 0)
   @CsvDate(value = "yyyy-MM-dd", writeFormat = "yyyy-MM-dd")
   private LocalDate updatedDate = LocalDate.now();
}
