package domain.program;

import java.util.UUID;

public class Transmission extends Program{

    public Transmission(String name) {
        super(name);
    }

    public Transmission(UUID uuid, String name, String description, int createdBy, int duration, boolean approved, String production) {
        super(uuid, name, description, createdBy, duration, approved, production);
    }
}
