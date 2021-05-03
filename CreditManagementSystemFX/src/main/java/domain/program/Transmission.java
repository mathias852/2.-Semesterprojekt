package domain.program;

import java.util.UUID;
import domain.accesscontrol.User;

public class Transmission extends Program{

    public Transmission(String name) {
        super(name);
    }

    public Transmission(UUID uuid, String name, String description, String createdBy, int duration) {
        super(uuid, name, description, createdBy, duration);
    }
}
