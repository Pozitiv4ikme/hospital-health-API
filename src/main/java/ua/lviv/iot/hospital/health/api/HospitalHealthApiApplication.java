package ua.lviv.iot.hospital.health.api;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.lviv.iot.hospital.health.api.storage.PatientStorage;

@SpringBootApplication
@RequiredArgsConstructor
public class HospitalHealthApiApplication implements CommandLineRunner {
  private final PatientStorage patientStorage;

	public static void main(String[] args) {
		SpringApplication.run(HospitalHealthApiApplication.class, args);
	}

	@Override
	public void run(String[] args) throws IOException {
		patientStorage.loadFromFile();
	}
}
