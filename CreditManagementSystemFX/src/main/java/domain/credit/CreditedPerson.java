package domain.credit;

import java.util.UUID;

public class CreditedPerson {

    private String name;
    private UUID uuid;


    public CreditedPerson(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
