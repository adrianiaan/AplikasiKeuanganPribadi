package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas helper untuk pengelolaan koneksi dan operasi dasar pada database.
 * Kelas ini mengelola koneksi ke database SQLite dan menyediakan metode
 * untuk inisialisasi tabel serta menutup koneksi.
 */
public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:keuangan.db"; // Lokasi file database SQLite
    private static Connection connection;  // Deklarasi koneksi database

    /**
     * Method untuk mendapatkan koneksi ke database.
     * Jika koneksi belum dibuat atau sudah tertutup, akan membuat koneksi baru.
     *
     * @return Connection koneksi ke database.
     * @throws SQLException jika terjadi kesalahan saat membuka koneksi.
     */
    public static Connection getConnection() throws SQLException {
        // Membuka koneksi jika belum ada atau sudah tertutup
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);  // Membuka koneksi baru
        }
        return connection;
    }

    /**
     * Method untuk inisialisasi database dengan membuat tabel transaksi
     * jika tabel tersebut belum ada di database.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // SQL untuk membuat tabel transaksi jika belum ada
            String createTableSQL = "CREATE TABLE IF NOT EXISTS transaksi ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // ID otomatis sebagai primary key
                    + "tanggal DATE NOT NULL, "  // Kolom tanggal transaksi
                    + "deskripsi TEXT NOT NULL, "  // Kolom deskripsi transaksi
                    + "kategori TEXT CHECK(kategori IN ('Pemasukan', 'Pengeluaran')) NOT NULL, "  // Kolom kategori dengan validasi nilai
                    + "jumlah REAL NOT NULL"  // Kolom jumlah transaksi
                    + ");";

            stmt.execute(createTableSQL); // Menjalankan query SQL untuk membuat tabel
            System.out.println("Tabel transaksi berhasil dibuat atau sudah ada."); // Pesan log keberhasilan
        } catch (SQLException e) {
            e.printStackTrace(); // Menampilkan stack trace jika terjadi kesalahan
        }
    }

    /**
     * Method untuk menutup koneksi ke database.
     * Pastikan koneksi sudah tidak digunakan sebelum memanggil method ini.
     *
     * @throws SQLException jika terjadi kesalahan saat menutup koneksi.
     */
    public static void closeConnection() throws SQLException {
        // Memastikan koneksi tidak null dan masih terbuka
        if (connection != null && !connection.isClosed()) {
            connection.close();  // Menutup koneksi database
            System.out.println("Koneksi database ditutup."); // Pesan log saat koneksi ditutup
        }
    }
}
