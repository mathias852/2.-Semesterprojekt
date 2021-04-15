package domain.credit;

import java.util.UUID;

public class CreditedPerson {

    private String name;
    private UUID uuid;


    public CreditedPerson(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
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
