package ua.lviv.iot.hospital.health.api.model.entity;

import java.util.List;

public record Hospital(int id,
                       List<Patient> patients) {
}
