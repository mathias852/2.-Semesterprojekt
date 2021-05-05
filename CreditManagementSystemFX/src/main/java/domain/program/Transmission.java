package domain.program;

import java.util.UUID;
import domain.accesscontrol.User;

public class Transmission extends Program{

    public Transmission(String name) {
        super(name);
    }

    public Transmission(UUID uuid, String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        super(uuid, name, description, createdBy, duration, approved, production);
    }
}
