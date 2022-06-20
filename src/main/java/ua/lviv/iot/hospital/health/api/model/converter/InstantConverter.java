package ua.lviv.iot.hospital.health.api.model.converter;

import com.opencsv.bean.AbstractBeanField;
import java.time.Instant;

public final class InstantConverter extends AbstractBeanField<Instant, String> {

  @Override
  protected Object convert(final String value) {
    return Instant.parse(value);
  }
}
