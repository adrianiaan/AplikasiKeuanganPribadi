# Aplikasi Keuangan Pribadi

Aplikasi Keuangan Pribadi adalah aplikasi desktop berbasis Java yang digunakan untuk mengelola transaksi keuangan pribadi. Aplikasi ini memungkinkan pengguna untuk mencatat pemasukan dan pengeluaran, serta menghasilkan laporan dalam format PDF dan CSV. Data disimpan menggunakan database SQLite.

## Fitur
- Menambah, mengubah, dan menghapus transaksi
- Mencari transaksi berdasarkan kategori dan deskripsi
- Menyimpan dan memuat data transaksi dari file CSV
- Membuat laporan transaksi dalam format PDF
- Validasi input jumlah transaksi (hanya angka)
- Tampilan antarmuka grafis dengan JTable

## Struktur Proyek

### 1. **Model**
Model berisi dua kelas utama:
- `Keuangan`: Kelas induk yang menyimpan data umum seperti ID dan tanggal transaksi.
- `Transaksi`: Kelas turunan yang menyimpan detail transaksi seperti deskripsi, kategori (pemasukan/pengeluaran), dan jumlah.

```java
public class Keuangan {
    private int id;
    private Date tanggal;

    public Keuangan(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Keuangan(int id, Date tanggal) {
        this.id = id;
        this.tanggal = tanggal;
    }

    // Getter dan Setter
}

public class Transaksi extends Keuangan {
    private String deskripsi;
    private Kategori kategori;
    private double jumlah;

    public Transaksi(Date tanggal, String deskripsi, Kategori kategori, double jumlah) {
        super(tanggal);
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.jumlah = jumlah;
    }

    // Getter dan Setter
}
```

### 2. **Controller**
Controller mengatur logika aplikasi, seperti menambah, mengubah, menghapus transaksi, serta memuat dan menyimpan data ke file CSV atau database.

Metode utama:
- `tambahTransaksi(Transaksi transaksi)`
- `hapusTransaksi(int id)`
- `ubahTransaksi(Transaksi transaksi)`
- `tampilkanKeTabel(DefaultTableModel model)`
- `muatDataDariFile(String filePath, DefaultTableModel model)`
- `simpanDataKeCSV(DefaultTableModel model, String filePath)`
- `buatLaporanPDF(String filePath)`

```java
public class TransaksiController {
    public void tambahTransaksi(Transaksi transaksi) {
        // Implementasi untuk menambah transaksi
    }

    public void hapusTransaksi(int id) {
        // Implementasi untuk menghapus transaksi
    }

    public void ubahTransaksi(Transaksi transaksi) {
        // Implementasi untuk mengubah transaksi
    }

    // Metode lainnya
}
```

### 3. **Database**
Aplikasi ini menggunakan database SQLite untuk menyimpan data transaksi. Tabel utama adalah `transaksi` yang menyimpan ID, tanggal, deskripsi, kategori, dan jumlah.

```java
public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:keuangan.db";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        // SQL untuk membuat tabel transaksi
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
```

### 4. **Tampilan (GUI)**
Aplikasi ini menggunakan JFrame sebagai antarmuka pengguna untuk menampilkan tabel transaksi dan memungkinkan pengguna untuk berinteraksi dengan aplikasi. Tombol utama mencakup:
- `btnTambah`: Menambah transaksi baru
- `btnUbah`: Mengubah transaksi yang sudah ada
- `btnHapus`: Menghapus transaksi yang dipilih
- `btnPencarian`: Mencari transaksi berdasarkan kategori dan deskripsi
- `btnSimpan`: Menyimpan data ke file CSV
- `btnMuat`: Memuat data dari file CSV
- `btnLaporan`: Membuat laporan PDF

```java
public class TampilanUtama extends javax.swing.JFrame {
    private TransaksiController transaksiController;
    private DefaultTableModel tabelModel;

    public TampilanUtama() {
        // Inisialisasi dan event handler
    }

    // Event handler untuk tombol
    btnTambah.addActionListener(e -> {
        // Logika untuk menambah transaksi
    });

    btnUbah.addActionListener(e -> {
        // Logika untuk mengubah transaksi
    });

    // Event handler lainnya
}
```

## Metode dan Events
- **Menambahkan Transaksi**: Ketika pengguna mengisi form dan menekan tombol `Tambah`, data akan diproses dan disimpan ke database.
- **Mencari Transaksi**: Pengguna dapat mencari transaksi berdasarkan kategori dan deskripsi dengan menggunakan `btnPencarian`.
- **Mengimpor dan Mengekspor Data**: Data transaksi dapat dimuat dari file CSV menggunakan `btnMuat` dan disimpan ke file CSV menggunakan `btnSimpan`.
- **Laporan PDF**: Menghasilkan laporan transaksi dalam format PDF dengan `btnLaporan`.

## Cara Penggunaan
1. **Menambah Transaksi**: Isi form dengan tanggal, deskripsi, kategori, dan jumlah transaksi lalu tekan `Tambah`.
2. **Mengubah Transaksi**: Pilih transaksi di tabel, ubah form, lalu tekan `Ubah`.
3. **Menghapus Transaksi**: Pilih transaksi di tabel dan tekan `Hapus`.
4. **Mencari Transaksi**: Masukkan kata kunci pencarian dan pilih kategori, lalu tekan `Pencarian`.
5. **Muat dan Simpan Data**: Gunakan tombol `Muat` untuk memuat data dari file CSV dan `Simpan` untuk menyimpan data ke CSV.

## Persyaratan
- Java 8 atau lebih tinggi
- SQLite JDBC Driver
- iText PDF Library (untuk membuat laporan PDF)

## Instalasi
1. Clone repository ini ke komputer Anda:
   ```
   git clone https://github.com/adrianiaan/KeuanganPribadi.git
   ```
2. Kompilasi dan jalankan menggunakan IDE seperti IntelliJ IDEA atau NetBeans.

