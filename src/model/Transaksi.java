// Kelas Transaksi sebagai kelas turunan (Child Class)
package model;

public class Transaksi extends Keuangan {
    private String deskripsi; // Deskripsi transaksi
    private String kategori;  // Kategori transaksi (Pemasukan atau Pengeluaran)
    private double jumlah;    // Jumlah uang dalam transaksi

    // Constructor: Menginisialisasi atribut dari parent dan child class
    public Transaksi(String id, String tanggal, String deskripsi, String kategori, double jumlah) {
        super(id, tanggal); // Memanggil constructor dari parent class (Keuangan)
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
    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
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
