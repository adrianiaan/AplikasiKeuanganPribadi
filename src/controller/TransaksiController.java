// Package controller untuk logika aplikasi
package controller;

import model.Transaksi; // Import kelas Transaksi
import java.util.ArrayList; // Menggunakan ArrayList untuk menyimpan data
import javax.swing.table.DefaultTableModel; // Model tabel untuk JTable

public class TransaksiController {
    // List untuk menyimpan transaksi
    private ArrayList<Transaksi> daftarTransaksi;

    // Constructor: Menginisialisasi ArrayList
    public TransaksiController() {
        daftarTransaksi = new ArrayList<>();
    }

    // Method untuk menambah transaksi
    public void tambahTransaksi(Transaksi transaksi) {
        daftarTransaksi.add(transaksi); // Menambahkan transaksi ke list
    }

    // Method untuk mengubah transaksi berdasarkan index
    public void ubahTransaksi(int index, Transaksi transaksi) {
        daftarTransaksi.set(index, transaksi); // Mengubah data pada index tertentu
    }

    // Method untuk menghapus transaksi berdasarkan index
    public void hapusTransaksi(int index) {
        daftarTransaksi.remove(index); // Menghapus data pada index tertentu
    }

    // Method untuk menampilkan data transaksi ke JTable
    public void tampilkanKeTabel(DefaultTableModel model) {
        // Membersihkan tabel sebelum menampilkan data baru
        model.setRowCount(0);

        // Menambahkan setiap transaksi ke model tabel
        for (Transaksi t : daftarTransaksi) {
            model.addRow(new Object[]{
                t.getId(),
                t.getTanggal(),
                t.getDeskripsi(),
                t.getKategori(),
                t.getJumlah()
            });
        }
    }
}
