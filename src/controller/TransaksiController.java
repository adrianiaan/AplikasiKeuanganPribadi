package controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Transaksi;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import javax.swing.JOptionPane;

public class TransaksiController {

    // Method untuk menambah transaksi ke database
    public void tambahTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO transaksi (tanggal, deskripsi, kategori, jumlah) VALUES (?, ?, ?, ?)";
        try (Connection conn = database.DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(transaksi.getTanggal().getTime())); // Konversi tanggal ke SQL Date
            pstmt.setString(2, transaksi.getDeskripsi()); // Deskripsi transaksi
            pstmt.setString(3, transaksi.getKategori().toString()); // Enum kategori menjadi String
            pstmt.setDouble(4, transaksi.getJumlah()); // Jumlah transaksi
            pstmt.executeUpdate(); // Menjalankan query untuk menambah transaksi
            System.out.println("Transaksi berhasil ditambahkan!"); // Log untuk memastikan transaksi ditambahkan
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menghapus transaksi dari database
    public void hapusTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = database.DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mengubah transaksi di database
    public void ubahTransaksi(Transaksi transaksi) {
        String sql = "UPDATE transaksi SET tanggal = ?, deskripsi = ?, kategori = ?, jumlah = ? WHERE id = ?";
        try (Connection conn = database.DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(transaksi.getTanggal().getTime())); // Konversi tanggal
            pstmt.setString(2, transaksi.getDeskripsi());
            pstmt.setString(3, transaksi.getKategori().toString());
            pstmt.setDouble(4, transaksi.getJumlah()); // Simpan sebagai double
            pstmt.setInt(5, transaksi.getId());
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Transaksi berhasil diubah. ID: " + transaksi.getId());
            } else {
                System.out.println("Tidak ada transaksi yang diubah.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengubah transaksi. Error: " + e.getMessage());
        }
    }

    // Method untuk menampilkan data transaksi dari database ke JTable
    public void tampilkanKeTabel(DefaultTableModel model) {
        String sql = "SELECT * FROM transaksi";
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        try (Connection conn = database.DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            model.setRowCount(0); // Reset data JTable

            while (rs.next()) {
                Date tanggal = rs.getDate("tanggal");
                String formattedTanggal = (tanggal != null) ? dateFormat.format(tanggal) : "";
                double jumlah = rs.getDouble("jumlah");

                model.addRow(new Object[]{
                    rs.getInt("id"),
                    formattedTanggal,
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    formatRupiah.format(jumlah) // Hanya format tampilan
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk Muat data transaksi dari File .CSV ke JTable
    public void muatDataDariFile(String filePath, DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            model.setRowCount(0); // Hapus data lama dari tabel

            boolean isHeader = true; // Gunakan ini untuk melewati header
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Lewati baris pertama (header)
                    continue;
                }

                String[] values = line.split(","); // Asumsikan data dipisahkan dengan koma

                try {
                    if (values.length < 5) {
                        System.err.println("Data tidak lengkap pada baris: " + line);
                        continue; // Lewati baris jika tidak cukup kolom
                    }

                    String nomor = values[0].trim(); // Kolom nomor
                    String tanggalString = values[1].trim(); // Kolom tanggal
                    String deskripsi = values[2].trim(); // Kolom deskripsi
                    String kategori = values[3].trim(); // Kolom kategori
                    String jumlahString = values[4].trim(); // Kolom jumlah

                    // Validasi dan parsing tanggal
                    Date tanggal = null;
                    try {
                        tanggal = dateFormat.parse(tanggalString); // Parsing tanggal
                    } catch (Exception ex) {
                        System.err.println("Kesalahan parsing tanggal pada baris: " + line);
                        continue; // Lewati jika tanggal salah
                    }

                    // Validasi dan parsing jumlah
                    double jumlah = 0;
                    try {
                        // Bersihkan format angka (hanya gunakan jika ada simbol seperti `.` atau `,`)
                        jumlahString = jumlahString
                                .replace("Rp", "") // Hapus simbol Rupiah
                                .replace(",", "") // Hapus koma (untuk pemisah ribuan)
                                .trim();

                        // Parsing jumlah menjadi double
                        jumlah = Double.parseDouble(jumlahString);
                    } catch (Exception ex) {
                        System.err.println("Kesalahan parsing jumlah pada baris: " + line);
                        continue; // Lewati jika jumlah salah
                    }

                    // Tambahkan data ke tabel
                    model.addRow(new Object[]{
                        nomor, // Nomor
                        dateFormat.format(tanggal), // Format tanggal
                        deskripsi, // Deskripsi
                        kategori, // Kategori
                        formatRupiah.format(jumlah) // Format jumlah sebagai Rupiah
                    });
                } catch (Exception ex) {
                    System.err.println("Kesalahan memuat data pada baris: " + line);
                }
            }
            System.out.println("Data berhasil dimuat dari file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat memuat data dari file.");
        }
    }

    // Method untuk Menyimpan data transaksi dari database ke File .CSV
    public void simpanDataKeCSV(DefaultTableModel model, String filePath) {
        File file = new File(filePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            // Tulis header
            bufferedWriter.write("No,Tanggal,Deskripsi,Kategori,Jumlah");
            bufferedWriter.newLine();

            for (int i = 0; i < model.getRowCount(); i++) {
                String nomor = String.valueOf(i + 1); // Nomor urut
                String tanggal = model.getValueAt(i, 1).toString(); // Kolom tanggal
                String deskripsi = model.getValueAt(i, 2).toString(); // Deskripsi
                String kategori = model.getValueAt(i, 3).toString(); // Kategori
                String jumlahString = model.getValueAt(i, 4).toString()
                        .replace("Rp", "") // Hapus simbol Rupiah
                        .replace(".", "") // Hapus titik ribuan
                        .replace(",", ".") // Ganti koma dengan titik untuk desimal
                        .trim();

                try {
                    double jumlah = Double.parseDouble(jumlahString); // Parsing ke double
                    bufferedWriter.write(nomor + "," + tanggal + "," + deskripsi + "," + kategori + "," + jumlah);
                    bufferedWriter.newLine();
                } catch (Exception ex) {
                    System.err.println("Kesalahan pada baris ke-" + (i + 1) + ": " + ex.getMessage());
                }
            }
            System.out.println("Data berhasil disimpan ke file CSV: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan ke file CSV.");
        }
    }

    // Method untuk menulis ulang data ke file CSV
    public void tulisUlangCSV(DefaultTableModel model, String filePath) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("No,Tanggal,Deskripsi,Kategori,Jumlah");
            writer.newLine();

            for (int i = 0; i < model.getRowCount(); i++) {
                String nomor = String.valueOf(i + 1);
                String tanggal = model.getValueAt(i, 1).toString();
                String deskripsi = model.getValueAt(i, 2).toString();
                String kategori = model.getValueAt(i, 3).toString();
                String jumlahString = model.getValueAt(i, 4).toString()
                        .replace("Rp", "") // Hapus simbol Rp
                        .replace(".", "") // Hapus titik ribuan
                        .replace(",", ".") // Ganti koma dengan titik
                        .trim();

                try {
                    double jumlah = Double.parseDouble(jumlahString); // Parsing ke double
                    writer.write(nomor + "," + tanggal + "," + deskripsi + "," + kategori + "," + jumlah);
                    writer.newLine();
                } catch (Exception e) {
                    System.err.println("Kesalahan pada baris ke-" + (i + 1) + ": " + e.getMessage());
                }
            }
            System.out.println("Data berhasil disinkronkan ke file CSV: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menulis ulang file CSV.");
        }
    }

    // Method untuk Validasi data transaksi dari File .CSV ke JTable
    public boolean validasiFileCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null || !header.equals("ID,Tanggal,Deskripsi,Kategori,Jumlah")) {
                return false; // Header tidak sesuai
            }

            // Cek setiap baris data
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 5) {
                    return false; // Jumlah kolom tidak sesuai
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true; // Format file valid
    }

    // Method untuk membuat laporan PDF
    public void buatLaporanPDF(String filePath) {
        try {
            // Membuat dokumen PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Menambahkan tanggal laporan
            String currentDate = getFormattedDate(new Date());  // Mendapatkan tanggal terformat
            document.add(new Paragraph("Laporan Transaksi Keuangan - " + currentDate));  // Menambahkan tanggal laporan
            document.add(new Paragraph(" "));

            // Menambahkan laporan pemasukan
            document.add(new Paragraph("Laporan Pemasukan"));
            document.add(new Paragraph(" "));
            document.add(buatTabelTransaksi("Pemasukan"));  // Tabel untuk Pemasukan
            document.add(new Paragraph(" "));

            // Menambahkan laporan pengeluaran
            document.add(new Paragraph("Laporan Pengeluaran"));
            document.add(new Paragraph(" "));
            document.add(buatTabelTransaksi("Pengeluaran"));  // Tabel untuk Pengeluaran

            // Menutup dokumen PDF
            document.close();
            System.out.println("Laporan berhasil dibuat!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Format tanggal dalam Bahasa Indonesia tanpa jam
    private String getFormattedDate(Date date) {
        // Gunakan SimpleDateFormat untuk format tanggal dengan Locale Indonesia
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
        return sdf.format(date); // Formatkan tanggal dan kembalikan
    }

    // Method untuk membuat tabel transaksi dalam PDF
    private PdfPTable buatTabelTransaksi(String kategori) throws SQLException, DocumentException {
        PdfPTable table = new PdfPTable(5); // 5 kolom: Nomor, Tanggal, Deskripsi, Kategori, Jumlah
        table.addCell("No");
        table.addCell("Tanggal");
        table.addCell("Deskripsi");
        table.addCell("Kategori");
        table.addCell("Jumlah");

        // Format Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));

        String sql = "SELECT * FROM transaksi WHERE kategori = ?";
        double total = 0;  // Menyimpan total transaksi untuk kategori ini

        try (Connection conn = database.DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategori); // Set kategori yang sesuai
            ResultSet rs = pstmt.executeQuery();

            // Mengisi tabel PDF dengan data transaksi
            int nomor = 1;  // Nomor urut untuk transaksi
            while (rs.next()) {
                table.addCell(String.valueOf(nomor));
                table.addCell(dateFormat.format(rs.getDate("tanggal")));  // Menggunakan format tanggal
                table.addCell(rs.getString("deskripsi"));
                table.addCell(rs.getString("kategori"));
                table.addCell(formatRupiah.format(rs.getDouble("jumlah")));  // Format jumlah uang

                total += rs.getDouble("jumlah");

                nomor++;
            }

            // Menambahkan total transaksi di bawah tabel
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("Total " + kategori);
            table.addCell(formatRupiah.format(total));  // Tampilkan total dengan format Rupiah
        }

        return table;
    }

    // Method untuk Pencarian data transaksi di JTable
    public void cariTransaksi(DefaultTableModel model, String kategori, String pencarian) {
        String sql = "SELECT * FROM transaksi WHERE kategori = ? AND (deskripsi LIKE ?)";
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        try (Connection conn = database.DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kategori);  // Set kategori dari ComboBox
            pstmt.setString(2, "%" + pencarian + "%");  // Pencarian berdasarkan deskripsi (LIKE)

            ResultSet rs = pstmt.executeQuery();
            model.setRowCount(0); // Reset tabel

            while (rs.next()) {
                Date tanggal = rs.getDate("tanggal");
                String formattedTanggal = (tanggal != null) ? dateFormat.format(tanggal) : "";
                double jumlah = rs.getDouble("jumlah");

                model.addRow(new Object[]{
                    rs.getInt("id"),
                    formattedTanggal,
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    formatRupiah.format(jumlah) // Format jumlah uang
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
