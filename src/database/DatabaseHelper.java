package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:keuangan.db"; // Lokasi file database SQLite
    private static Connection connection;  // Deklarasi koneksi

    // Method untuk mendapatkan koneksi ke database
    public static Connection getConnection() throws SQLException {
        // Membuka koneksi jika belum ada atau sudah tertutup
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);  // Membuka koneksi
        }
        return connection;
    }

    // Method untuk membuat tabel jika belum ada
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // SQL untuk membuat tabel transaksi jika belum ada
            String createTableSQL = "CREATE TABLE IF NOT EXISTS transaksi ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // ID otomatis
                    + "tanggal DATE NOT NULL, "  // Tanggal transaksi
                    + "deskripsi TEXT NOT NULL, "  // Deskripsi transaksi
                    + "kategori TEXT CHECK(kategori IN ('Pemasukan', 'Pengeluaran')) NOT NULL, "  // Kategori transaksi
                    + "jumlah REAL NOT NULL"  // Jumlah transaksi
                    + ");";

            stmt.execute(createTableSQL); // Menjalankan query untuk membuat tabel
            System.out.println("Tabel transaksi berhasil dibuat atau sudah ada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menutup koneksi
    public static void closeConnection() throws SQLException {
        // Pastikan koneksi tidak null dan masih terbuka
        if (connection != null && !connection.isClosed()) {
            connection.close();  // Menutup koneksi database
            System.out.println("Koneksi database ditutup.");
        }
    }
}
