package persistence;

public class DatabaseConfig {
    static String databaseName = "CreditsManagementDatabase";
    static String username = "postgres";
    static String password = "";
    public static String getDatabaseName() {
        return databaseName;
    }
    public static String getUsername() {
        return username;
    }
    public static String getPassword() {
        return password;
    }
}
