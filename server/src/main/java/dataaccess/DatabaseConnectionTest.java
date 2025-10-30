package dataaccess;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        try (var conn = DatabaseManager.getConnection()) {
            if (conn != null) {
                System.out.println("✅ SUCCESS: Database connection established!");
                System.out.println("Connected to: " + conn.getMetaData().getURL());
            } else {
                System.out.println("❌ Connection object is null.");
            }
        } catch (DataAccessException e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

