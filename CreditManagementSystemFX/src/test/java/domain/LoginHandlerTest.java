package domain;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class LoginHandlerTest {
    LoginHandler loginHandler;

    @Before
    public void setUp() {
        loginHandler = LoginHandler.getInstance();
    }

    @Test
    public void createProducer() {
        UUID uuid = UUID.randomUUID();
        assertTrue(loginHandler.createProducer(uuid, "TestUser2", "12345678".hashCode()));
        assertNotNull(loginHandler.getUserFromUuid(uuid));
    }

    @Test
    public void verifyCredentials() {
        loginHandler.createProducer(UUID.randomUUID(), "TestUser", "12345678".hashCode());
        assertNotNull(loginHandler.verifyCredentials("TestUser", "12345678".hashCode()));
    }
}