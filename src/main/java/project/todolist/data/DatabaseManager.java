package project.todolist.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:todolist.db"; // Nama file database Anda
    private static Connection connection;

    // Metode untuk mendapatkan koneksi
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC"); // Pastikan driver SQLite ada
                connection = DriverManager.getConnection(DB_URL);
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC Driver not found.");
                throw new SQLException("SQLite JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    // Metode untuk menutup koneksi
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing SQLite connection: " + e.getMessage());
            }
        }
    }

    // Metode untuk membuat tabel jika belum ada
    public static void initializeDatabase() {
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        String createTodoItemsTableSql = "CREATE TABLE IF NOT EXISTS todo_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "title TEXT NOT NULL,"
                + "tanggal TEXT NOT NULL,"
                + "waktu TEXT NOT NULL,"
                + "catatan TEXT,"
                + "kategori TEXT,"
                + "status TEXT,"
                + "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableSql);
            stmt.execute(createTodoItemsTableSql);
            System.out.println("Database tables created or already exist.");
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
        }
    }

    // Panggil initializeDatabase() saat aplikasi pertama kali dimulai
    // Misalnya, di main method atau di constructor Application Anda.
    public static void main(String[] args) {
        // Contoh penggunaan
        initializeDatabase();
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Successfully connected to SQLite database.");
                closeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQLite database: " + e.getMessage());
        }
    }
}