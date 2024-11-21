package model;

import java.util.Date;

public class Keuangan {
    private int id; // Menggunakan int untuk ID
    private Date tanggal; // Menggunakan Date untuk tanggal

    // Constructor: Menginisialisasi atribut
    public Keuangan(int id, Date tanggal) {
        this.id = id;
        this.tanggal = tanggal;
    }

    // Getter dan Setter untuk ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter dan Setter untuk Tanggal
    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    // Method getInfo untuk menampilkan informasi umum
    public String getInfo() {
        return "ID: " + id + ", Tanggal: " + tanggal;
    }
}
