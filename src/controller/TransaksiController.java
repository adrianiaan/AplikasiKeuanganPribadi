package controller;

import model.Transaksi;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class TransaksiController {

    // Method untuk menambah transaksi ke database
    public void tambahTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO transaksi (id, tanggal, deskripsi, kategori, jumlah) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaksi.getId());
            pstmt.setDate(2, new java.sql.Date(transaksi.getTanggal().getTime()));
            pstmt.setString(3, transaksi.getDeskripsi());
            pstmt.setString(4, transaksi.getKategori().toString());
            pstmt.setDouble(5, transaksi.getJumlah());
            pstmt.executeUpdate();
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
            pstmt.setDate(1, new java.sql.Date(transaksi.getTanggal().getTime()));
            pstmt.setString(2, transaksi.getDeskripsi());
            pstmt.setString(3, transaksi.getKategori().toString());
            pstmt.setDouble(4, transaksi.getJumlah());
            pstmt.setInt(5, transaksi.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menampilkan data transaksi dari database ke JTable
    public void tampilkanKeTabel(DefaultTableModel model) {
        String sql = "SELECT * FROM transaksi";
        try (Connection conn = database.DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getDate("tanggal"),
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    rs.getDouble("jumlah")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
