package persistence;

public class DatabaseConfig {
    static String databaseName = "CreditManagementSystem";
    static String username = "postgres";
    static String password = "d4t4b4s3_Learning";
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
