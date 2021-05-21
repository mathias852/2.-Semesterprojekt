package domain.program;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ProgramTest {
    Program program;
    Credit c;
    //java.util.UUID uuid, String name, String description, UUID createdBy, int duration, boolean approved, String production

    @Before
    public void setUp() {
        c = new Credit(new CreditedPerson(UUID.randomUUID(), "TestPerson"), Credit.Function.PRODUCTIONCOORDINATOR);
        program = new Transmission(UUID.randomUUID(), "ProgramTest", "ProgramTestDescription", UUID.randomUUID(), 120, false, "TV2");
    }

    @Test
    public void addCredit() {
        program.addCredit(c);
        assertNotNull(program.getCredits());
    }

    @Test
    public void deleteCredit() {
        program.deleteCredit(c);
        assertNull(program.getCredits());
    }
}