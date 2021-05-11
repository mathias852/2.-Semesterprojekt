package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PersistenceHandler {
    private static PersistenceHandler instance;
    private Connection connection = null;

    private PersistenceHandler(){
        initializePostgresqlDatabase();
    }

    public static PersistenceHandler getInstance(){
        if (instance == null) {
            instance = new PersistenceHandler();
        }
        return instance;
    }

    private void initializePostgresqlDatabase() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "localhost";
            int port = 5432;
            connection = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" +
                    DatabaseConfig.getDatabaseName(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace(System.err);
        } finally {
            if (connection == null) System.exit(-1);
        }
    }

}
