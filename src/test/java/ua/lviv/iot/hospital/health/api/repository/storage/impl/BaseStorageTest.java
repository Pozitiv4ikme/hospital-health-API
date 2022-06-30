package ua.lviv.iot.hospital.health.api.repository.storage.impl;

import java.io.File;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaseStorageTest {

  @Value("${storage.folder}")
  private String folderName;

  @BeforeEach
  void setup() {
    removeFiles();
  }

  @AfterEach
  void cleanup() {
    removeFiles();
  }

  void removeFiles() {
    var folder = new File(folderName);
    Arrays.stream(folder.listFiles()).forEach(File::delete);
  }

}
