package domain.program;

import java.util.UUID;

public class Transmission extends Program{

    public Transmission(String name) {
        super(name);
    }

    public Transmission(UUID uuid, String name, String description, int eventID, int createdBy, int duration) {
        super(uuid, name, description, eventID, createdBy, duration);
    }
}
