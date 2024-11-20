// Package model untuk menyimpan kelas-kelas data
package model;

// Kelas Keuangan sebagai kelas induk (Parent Class)
public class Keuangan {
    private String id; // ID transaksi
    private String tanggal; // Tanggal transaksi

    // Constructor: Menginisialisasi atribut
    public Keuangan(String id, String tanggal) {
        this.id = id;
        this.tanggal = tanggal;
    }

    // Getter dan Setter untuk ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter dan Setter untuk Tanggal
    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    // Method getInfo untuk menampilkan informasi umum
    public String getInfo() {
        return "ID: " + id + ", Tanggal: " + tanggal;
    }
}
