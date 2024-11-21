package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:keuangan.db"; // Lokasi file database SQLite
    private static Connection connection;

    // Method untuk mendapatkan koneksi ke database
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            // Membuka koneksi ke database SQLite, file "keuangan.db" akan dibuat jika belum ada
            connection = DriverManager.getConnection(DATABASE_URL);
        }
        return connection;
    }

    // Method untuk membuat tabel jika belum ada
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // SQL untuk membuat tabel transaksi dengan tipe data yang diminta
            String createTableSQL = "CREATE TABLE IF NOT EXISTS transaksi ("
                    + "id INTEGER PRIMARY KEY, "  // Menggunakan INTEGER untuk ID
                    + "tanggal DATE NOT NULL, "  // Menggunakan DATE untuk tanggal
                    + "deskripsi TEXT NOT NULL, "  // Menggunakan TEXT untuk deskripsi
                    + "kategori TEXT CHECK(kategori IN ('Pemasukan', 'Pengeluaran')) NOT NULL, "  // ENUM-like menggunakan CHECK
                    + "jumlah REAL NOT NULL"  // Menggunakan REAL untuk jumlah
                    + ");";

            stmt.execute(createTableSQL); // Menjalankan query untuk membuat tabel
            System.out.println("Tabel transaksi berhasil dibuat atau sudah ada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
