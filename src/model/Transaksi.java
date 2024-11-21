package model;

import java.util.Date;

public class Transaksi extends Keuangan {
    private String deskripsi;  // Deskripsi transaksi
    private Kategori kategori; // Kategori transaksi (ENUM)
    private double jumlah;     // Jumlah transaksi

    // Constructor tanpa ID untuk data baru
    public Transaksi(Date tanggal, String deskripsi, Kategori kategori, double jumlah) {
        super(tanggal); // Panggil constructor Keuangan tanpa ID
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.jumlah = jumlah;
    }

    // Constructor dengan ID untuk data yang sudah ada
    public Transaksi(int id, Date tanggal, String deskripsi, Kategori kategori, double jumlah) {
        super(id, tanggal); // Panggil constructor Keuangan dengan ID
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.jumlah = jumlah;
    }

    // Getter dan Setter untuk Deskripsi
    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Getter dan Setter untuk Kategori
    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    // Getter dan Setter untuk Jumlah
    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    // Overriding: Mengubah perilaku method getInfo dari parent class
    @Override
    public String getInfo() {
        return super.getInfo() + ", Deskripsi: " + deskripsi + ", Kategori: " + kategori + ", Jumlah: " + jumlah;
    }
}
