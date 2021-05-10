package domain.accesscontrol;

import java.util.UUID;

public class Producer extends User{

    public Producer(UUID uuid, String username, int password){
        super(uuid, username, password);
    }
}
