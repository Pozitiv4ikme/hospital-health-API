package ua.lviv.iot.hospital.health.api.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.lviv.iot.hospital.health.api.model.entity.Patient;

@Component
@RequiredArgsConstructor
public class PatientStorage {
  private static final Map<Integer, Patient> PATIENTS = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;

  @Value("${storage.patient-file}")
  private String patientFilePath;

  public void save(Patient patient) {
    PATIENTS.putIfAbsent(patient.id(), patient);
  }

  public List<Patient> getAll() {
    return List.copyOf(PATIENTS.values());
  }

  public Optional<Patient> getById(int id) {
    return Optional.ofNullable(PATIENTS.get(id));
  }

  public void loadFromFile() throws IOException {
    var patientList = objectMapper.readValue(Paths.get(patientFilePath).toFile(),
        new TypeReference<List<Patient>>(){});
    patientList.forEach(this::save);
  }
}
