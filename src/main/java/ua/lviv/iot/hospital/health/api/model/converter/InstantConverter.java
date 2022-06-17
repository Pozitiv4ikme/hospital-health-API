package ua.lviv.iot.hospital.health.api.model.converter;

import com.opencsv.bean.AbstractBeanField;
import java.time.Instant;

public class InstantConverter extends AbstractBeanField<Instant, String> {

  @Override
  protected Object convert(String value) {
    return Instant.parse(value);
  }
}
