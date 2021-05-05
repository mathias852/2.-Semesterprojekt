package domain.accesscontrol;

import java.util.UUID;

public class SystemAdmin extends User {

    public SystemAdmin(UUID uuid, String username, int password) {
        super(uuid, username, password);
    }
}
