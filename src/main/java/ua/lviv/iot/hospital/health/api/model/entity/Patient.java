package ua.lviv.iot.hospital.health.api.model.entity;

public record Patient(int id,
                      int hospitalId,
                      String name,
                      String surname,
                      int floor,
                      int building,
                      int room) {
}
