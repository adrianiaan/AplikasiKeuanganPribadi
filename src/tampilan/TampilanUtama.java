package tampilan;

import controller.TransaksiController;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Kategori;
import model.Transaksi;

/**
 *
 * @author ADRIAN WIN
 */
public class TampilanUtama extends javax.swing.JFrame {

    // Deklarasi TransaksiController dan DefaultTableModel
    private TransaksiController transaksiController;
    private DefaultTableModel tabelModel;

    public TampilanUtama() {
        initComponents();
        
        // Isi combo box kategori pencarian
        isiComboBoxKategoriPencarian();
        
        // Button Pencarian
        btnPencarian.addActionListener(e -> {
            String kategori = cmbKategoriPencarian.getSelectedItem().toString();
            String pencarian = txtPencarian.getText().trim();

            // Validasi jika pencarian kosong
            if (pencarian.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan kata kunci pencarian.");
                return;
            }

            // Panggil metode pencarian di controller
            transaksiController.cariTransaksi(tabelModel, kategori, pencarian);
        });


        // Menambahkan FocusListener untuk mengosongkan txtJumlahUang saat diklik
        txtJumlahUang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtJumlahUang.setText("");  // Kosongkan isi text field saat mendapatkan fokus
            }
        });

        // Menambahkan KeyListener untuk memvalidasi hanya angka pada txtJumlahUang
        txtJumlahUang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Pastikan hanya angka dan titik desimal yang diterima
                if (!Character.isDigit(c) && c != '.' && c != '\b') {
                    evt.consume();  // Menolak karakter yang tidak valid
                }
            }
        });

        // Menonaktifkan tombol 'Ubah' dan 'Hapus' pada saat aplikasi pertama kali dibuka
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);

        database.DatabaseHelper.initializeDatabase(); // Pastikan tabel 'transaksi' ada

        transaksiController = new TransaksiController(); // Inisialisasi controller
        tabelModel = new DefaultTableModel();
        tabelModel.setColumnIdentifiers(new String[]{"ID", "Tanggal", "Deskripsi", "Kategori", "Jumlah"});
        jTable1.setModel(tabelModel); // Hubungkan JTable dengan model tabel
        transaksiController.tampilkanKeTabel(tabelModel); // Load data dari database ke JTable

        // Sembunyikan kolom ID
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);

        // Tambahkan listener untuk JTable
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    // Ambil data dari JTable dan isi ke input
                    String deskripsi = tabelModel.getValueAt(selectedRow, 2).toString();
                    String kategori = tabelModel.getValueAt(selectedRow, 3).toString();
                    String jumlah = tabelModel.getValueAt(selectedRow, 4).toString();

                    txtAreaDeskripsi.setText(deskripsi);
                    cmbKategori.setSelectedItem(kategori);
                    txtJumlahUang.setText(jumlah);

                    // Aktifkan tombol 'Ubah' dan 'Hapus' jika baris dipilih
                    btnUbah.setEnabled(true);
                    btnHapus.setEnabled(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saat memuat data: " + ex.getMessage());
                }
            } else {
                // Jika tidak ada baris yang dipilih, non-aktifkan tombol 'Ubah' dan 'Hapus'
                btnUbah.setEnabled(false);
                btnHapus.setEnabled(false);
            }
        });

        isiComboBoxKategori();

        // Menambahkan WindowListener untuk menangani penutupan jendela
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    // Menutup koneksi database saat aplikasi ditutup
                    database.DatabaseHelper.closeConnection();
                    System.out.println("Aplikasi ditutup dan koneksi database ditutup.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    private void isiComboBoxKategoriPencarian() {
        // Hapus item yang ada dalam ComboBox
        cmbKategoriPencarian.removeAllItems();

        // Tambahkan kategori yang valid ke ComboBox
        for (Kategori kategori : Kategori.values()) {
            cmbKategoriPencarian.addItem(kategori.name()); // Menambahkan nama kategori (Pemasukan, Pengeluaran)
        }
    }


    private void isiComboBoxKategori() {
        // Hapus item yang ada dalam ComboBox
        cmbKategori.removeAllItems();

        // Tambahkan kategori yang valid (Pemasukan dan Pengeluaran) ke ComboBox
        for (Kategori kategori : Kategori.values()) {
            cmbKategori.addItem(kategori.name()); // Menambahkan nama kategori (Pemasukan, Pengeluaran) ke ComboBox
        }

        // Event handler untuk tombol tambah
        btnTambah.addActionListener(e -> {
            try {
                // Ambil data dari komponen input
                Date tanggal = jCalendar1.getDate();  // Ambil tanggal dari JCalendar
                String deskripsi = txtAreaDeskripsi.getText();  // Ambil deskripsi dari JTextArea
                Kategori kategori = Kategori.valueOf(cmbKategori.getSelectedItem().toString());  // Mengambil nilai kategori
                double jumlah = Double.parseDouble(txtJumlahUang.getText());  // Ambil jumlah dari JTextField

                // Buat objek Transaksi tanpa ID (ID otomatis dihasilkan)
                Transaksi transaksi = new Transaksi(tanggal, deskripsi, kategori, jumlah);

                // Simpan transaksi ke database melalui controller
                transaksiController.tambahTransaksi(transaksi);

                // Refresh tabel setelah data ditambahkan
                transaksiController.tampilkanKeTabel(tabelModel);

                // Reset form setelah input data baru
                txtAreaDeskripsi.setText("");  // Mengosongkan kolom deskripsi
                txtJumlahUang.setText("");  // Mengosongkan kolom jumlah uang
                cmbKategori.setSelectedIndex(0); // Reset ComboBox ke default

                JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!");  // Tampilkan pesan sukses
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnUbah.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    // Ambil ID dari baris yang dipilih
                    int id = Integer.parseInt(tabelModel.getValueAt(selectedRow, 0).toString());

                    // Ambil data dari komponen input
                    Date tanggal = jCalendar1.getDate();
                    String deskripsi = txtAreaDeskripsi.getText();
                    Kategori kategori = Kategori.valueOf(cmbKategori.getSelectedItem().toString());
                    double jumlah = Double.parseDouble(txtJumlahUang.getText());

                    // Buat objek Transaksi dengan ID
                    Transaksi transaksi = new Transaksi(id, tanggal, deskripsi, kategori, jumlah);

                    // Update data melalui controller
                    transaksiController.ubahTransaksi(transaksi);

                    // Refresh tabel setelah update
                    transaksiController.tampilkanKeTabel(tabelModel);

                    // Tampilkan pesan sukses
                    JOptionPane.showMessageDialog(this, "Transaksi berhasil diubah!");

                    // Kosongkan input setelah selesai
                    txtAreaDeskripsi.setText(""); // Mengosongkan kolom deskripsi
                    txtJumlahUang.setText(""); // Mengosongkan kolom jumlah uang
                    cmbKategori.setSelectedIndex(0); // Reset ComboBox ke default

                    // Non-aktifkan tombol 'Ubah' dan 'Hapus' setelah perubahan
                    btnUbah.setEnabled(false);
                    btnHapus.setEnabled(false);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih transaksi yang ingin diubah!");
            }
        });

        btnHapus.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    // Ambil ID dari baris yang dipilih
                    int id = Integer.parseInt(tabelModel.getValueAt(selectedRow, 0).toString());

                    // Hapus data melalui controller
                    transaksiController.hapusTransaksi(id);

                    // Refresh tabel
                    transaksiController.tampilkanKeTabel(tabelModel);

                    JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih transaksi yang ingin dihapus!");
            }
        });

        btnMuat.addActionListener(e -> {
            try {
                // Gunakan JFileChooser untuk memilih file CSV
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                int result = fileChooser.showOpenDialog(this);

                if (result == javax.swing.JFileChooser.CANCEL_OPTION) {
                    // Jika pembatalan, beri pesan ke pengguna
                    JOptionPane.showMessageDialog(this, "Proses muat data dibatalkan oleh pengguna.");
                    return; // Keluar dari method tanpa melanjutkan
                }

                // Dapatkan path file
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                // Muat data dari file ke JTable
                transaksiController.muatDataDariFile(filePath, tabelModel);
                JOptionPane.showMessageDialog(this, "Data berhasil dimuat dari file CSV!");

                // Menambahkan nomor urut pada kolom pertama
                for (int i = 0; i < tabelModel.getRowCount(); i++) {
                    tabelModel.setValueAt(i + 1, i, 0); // Kolom pertama untuk nomor urut
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnSimpan.addActionListener(e -> {
            try {
                // Gunakan JFileChooser untuk memilih lokasi penyimpanan file CSV
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                int result = fileChooser.showSaveDialog(this);

                if (result == javax.swing.JFileChooser.CANCEL_OPTION) {
                    // Jika pembatalan, beri pesan ke pengguna
                    JOptionPane.showMessageDialog(this, "Penyimpanan dibatalkan oleh pengguna.");
                    return; // Keluar dari method tanpa melanjutkan
                }

                // Dapatkan path file
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                // Pastikan file memiliki ekstensi .csv
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }

                // Simpan data ke file CSV
                transaksiController.simpanDataKeCSV(tabelModel, filePath);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke file CSV: " + filePath);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnSinkronisasi.addActionListener(e -> {
            try {
                // Gunakan JFileChooser untuk memilih lokasi file CSV
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setDialogTitle("Pilih file CSV untuk sinkronisasi");
                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == javax.swing.JFileChooser.CANCEL_OPTION) {
                    // Jika pembatalan, beri pesan ke pengguna
                    JOptionPane.showMessageDialog(this, "Sinkronisasi dibatalkan oleh pengguna.");
                    return; // Keluar dari method tanpa melanjutkan
                }

                // Dapatkan path file
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                // Pastikan file memiliki ekstensi .csv
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }

                // Sinkronisasi data ke file CSV
                transaksiController.tulisUlangCSV(tabelModel, filePath);
                JOptionPane.showMessageDialog(this, "Data berhasil disinkronkan ke file CSV di: " + filePath);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyinkronkan data: " + ex.getMessage());
            }
        });

        btnLaporan.addActionListener(e -> {
            try {
                // Tentukan lokasi file PDF untuk menyimpan laporan
                String filePath = "Laporan_Transaksi.pdf";

                // Memanggil fungsi dari controller untuk membuat laporan PDF
                transaksiController.buatLaporanPDF(filePath);

                // Tampilkan pesan sukses
                JOptionPane.showMessageDialog(this, "Laporan berhasil dibuat di: " + filePath);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblKategori = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox<>();
        lblJumlah = new javax.swing.JLabel();
        txtJumlahUang = new javax.swing.JTextField();
        lblDeksripsi = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaDeskripsi = new javax.swing.JTextArea();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        lblTgl = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnMuat = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnSinkronisasi = new javax.swing.JButton();
        btnLaporan = new javax.swing.JButton();
        txtPencarian = new javax.swing.JTextField();
        cmbKategoriPencarian = new javax.swing.JComboBox<>();
        btnPencarian = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        lblKategori.setText("Pilih Pemasukan / Pengeluarani");

        lblJumlah.setText("Jumlah Uang");

        lblDeksripsi.setText("Deksripsi");

        txtAreaDeskripsi.setColumns(20);
        txtAreaDeskripsi.setRows(5);
        jScrollPane2.setViewportView(txtAreaDeskripsi);

        lblTgl.setText("Pilih Tanggal");

        btnTambah.setText("Tambah");

        btnUbah.setText("Ubah");

        btnHapus.setText("Hapus");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblKategori)
                            .addGap(20, 20, 20)
                            .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblDeksripsi)
                                .addComponent(lblJumlah))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtJumlahUang, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addComponent(btnTambah)
                        .addGap(45, 45, 45)
                        .addComponent(btnUbah)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblTgl))
                            .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(83, 83, 83))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(btnHapus)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKategori)
                            .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJumlahUang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblJumlah))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(lblDeksripsi))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTgl)
                        .addGap(4, 4, 4)
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus))
                .addContainerGap())
        );

        jPanel1.add(jPanel2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnMuat.setText("Muat Data");

        btnSimpan.setText("Simpan Data");

        btnSinkronisasi.setText("Sinkronsasi Data CSV");

        btnLaporan.setText("Cetak Laporan (PDF)");

        btnPencarian.setText("Cari");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(btnMuat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSinkronisasi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLaporan)
                        .addGap(0, 4, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbKategoriPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPencarian)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKategoriPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPencarian))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMuat)
                    .addComponent(btnSimpan)
                    .addComponent(btnSinkronisasi)
                    .addComponent(btnLaporan))
                .addContainerGap())
        );

        jPanel1.add(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TampilanUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TampilanUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TampilanUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TampilanUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TampilanUtama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnMuat;
    private javax.swing.JButton btnPencarian;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnSinkronisasi;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JComboBox<String> cmbKategoriPencarian;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblDeksripsi;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JTextArea txtAreaDeskripsi;
    private javax.swing.JTextField txtJumlahUang;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
