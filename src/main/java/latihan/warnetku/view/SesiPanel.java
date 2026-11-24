package latihan.warnetku.view;

import latihan.warnetku.dao.KomputerDao;
import latihan.warnetku.dao.KomputerDaoJdbc;
import latihan.warnetku.dao.PelangganDao;
import latihan.warnetku.dao.PelangganDaoJdbc;
import latihan.warnetku.dao.SesiDao;
import latihan.warnetku.dao.SesiDaoJdbc;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pelanggan;
import latihan.warnetku.model.Pengguna;
import latihan.warnetku.model.Sesi;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SesiPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private static final long serialVersionUID = 1L;
    private static final int KOLOM_SISA = 5;
    private static final String TEKS_HABIS = "HABIS";

    private final transient Pengguna petugas;
    private final transient KomputerDao komputerDao = new KomputerDaoJdbc();
    private final transient SesiDao sesiDao = new SesiDaoJdbc();
    private final transient PelangganDao pelangganDao = new PelangganDaoJdbc();
    private transient Pelanggan pelangganDipilih;
    private transient List<Sesi> berjalan = new ArrayList<>();
    private final DefaultTableModel model;
    private final Timer pencacah = new Timer(1000, e -> perbaruiWaktu());

    public SesiPanel(Pengguna petugas) {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        this.petugas = petugas;
        model = (DefaultTableModel) tabel.getModel();

        cmbKomputer.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object nilai, int indeks,
                                                          boolean terpilih, boolean fokus) {
                super.getListCellRendererComponent(list, nilai, indeks, terpilih, fokus);
                if (nilai instanceof Komputer) {
                    Komputer k = (Komputer) nilai;
                    setText(k.getNama() + "  |  " + k.getTipe());
                }
                return this;
            }
        });

        Tampilan.rapikanTabel(tabel);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(KOLOM_SISA).setCellRenderer(new Tampilan.RenderTeks(
                SwingConstants.CENTER, nilai -> TEKS_HABIS.equals(nilai) ? Tampilan.MERAH : Color.BLACK));

        txtPelanggan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                lupakanPelanggan();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                lupakanPelanggan();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                lupakanPelanggan();
            }
        });
        cmbKomputer.addActionListener(e -> tampilkanPelanggan());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        pencacah.start();
    }

    @Override
    public void removeNotify() {
        pencacah.stop();
        super.removeNotify();
    }

    @Override
    public void muatUlang() {
        Tampilan.coba(this, () -> {
            List<Komputer> tersedia = komputerDao.cariTersedia();
            berjalan = sesiDao.cariBerjalan();

            Komputer terpilih = (Komputer) cmbKomputer.getSelectedItem();
            cmbKomputer.setModel(new DefaultComboBoxModel<>(tersedia.toArray(new Komputer[0])));
            if (terpilih != null) {
                for (int i = 0; i < tersedia.size(); i++) {
                    if (tersedia.get(i).getId() == terpilih.getId()) {
                        cmbKomputer.setSelectedIndex(i);
                        break;
                    }
                }
            }
            lblTersedia.setText(tersedia.size() + " komputer tersedia");

            if (pelangganDipilih != null) {
                pelangganDipilih = pelangganDao.cariById(pelangganDipilih.getId()).orElse(null);
            }
            tampilkanPelanggan();

            model.setRowCount(0);
            for (Sesi s : berjalan) {
                model.addRow(new Object[]{s.getKode(), s.getKomputer().getNama(), s.getPelanggan().toString(),
                        Tampilan.jam(s.getWaktuMulai()), Tampilan.jam(s.getWaktuBerakhir()),
                        hitungSisaTeks(s, LocalDateTime.now())});
            }
            perbaruiWaktu();
        });
    }

    private void cekPelanggan() throws WarnetException {
        String teks = txtPelanggan.getText();
        String kode = Pelanggan.normalKode(teks);
        Pelanggan ditemukan = pelangganDao.cariByKode(kode)
                .orElseThrow(() -> new DataTidakValidException(
                        "Pelanggan dengan ID " + kode + " tidak ditemukan"));

        txtPelanggan.setText(ditemukan.getKode());
        pelangganDipilih = ditemukan;
        tampilkanPelanggan();
    }

    private void lupakanPelanggan() {
        pelangganDipilih = null;
        tampilkanPelanggan();
    }

    private void tampilkanPelanggan() {
        if (pelangganDipilih == null) {
            lblPelanggan.setText("Masukkan ID pelanggan lalu tekan Cek");
            lblPelanggan.setForeground(Tampilan.ABU);
            lblPelanggan.setFont(lblPelanggan.getFont().deriveFont(Font.PLAIN));
            return;
        }
        Komputer k = (Komputer) cmbKomputer.getSelectedItem();
        int billingDetik = k == null ? 0 : pelangganDipilih.getBillingDetik(k.getTipe());
        String teks = pelangganDipilih.getKode() + " - " + pelangganDipilih.getNama();
        if (k != null) {
            teks += "  |  Billing " + k.getTipe() + ": " + Tampilan.lamaDetik(billingDetik);
        }
        lblPelanggan.setText(teks);
        lblPelanggan.setForeground(billingDetik > 0 ? Tampilan.BIRU : Tampilan.MERAH);
        lblPelanggan.setFont(lblPelanggan.getFont().deriveFont(Font.BOLD));
    }

    private void pakaiSemuaBilling() {
        if (pelangganDipilih == null) {
            Tampilan.error(this, "Masukkan ID pelanggan dan tekan Cek terlebih dahulu");
            return;
        }
        Komputer k = (Komputer) cmbKomputer.getSelectedItem();
        if (k == null) {
            Tampilan.error(this, "Pilih komputer terlebih dahulu");
            return;
        }
        int detik = pelangganDipilih.getBillingDetik(k.getTipe());
        if (detik <= 0) {
            Tampilan.error(this, "Billing " + k.getTipe() + " untuk " + pelangganDipilih.getNama() + " kosong");
            return;
        }
        int menit = Math.min(Sesi.MENIT_MAKS, detik / 60);
        spnJam.setValue(menit / 60);
        spnMenit.setValue(menit % 60);
    }

    private int durasiMenitDipilih() throws DataTidakValidException {
        try {
            spnJam.commitEdit();
            spnMenit.commitEdit();
        } catch (ParseException e) {
            throw new DataTidakValidException("Lama main harus berupa angka");
        }
        int jam = ((Number) spnJam.getValue()).intValue();
        int menit = ((Number) spnMenit.getValue()).intValue();
        int total = jam * 60 + menit;
        if (total < 1 || total > Sesi.MENIT_MAKS) {
            throw new DataTidakValidException("Lama main minimal 1 menit dan maksimal 24 jam");
        }
        return total;
    }

    private void mulai() throws WarnetException {
        if (pelangganDipilih == null) {
            cekPelanggan();
        }
        Komputer k = (Komputer) cmbKomputer.getSelectedItem();
        if (k == null) {
            throw new DataTidakValidException("Pilih komputer terlebih dahulu");
        }
        int menit = durasiMenitDipilih();
        Sesi sesi = new Sesi(k, petugas, pelangganDipilih, menit);
        sesiDao.mulai(sesi);
        txtPelanggan.setText("");
        pelangganDipilih = null;
        spnJam.setValue(1);
        spnMenit.setValue(0);
        muatUlang();
        Tampilan.info(this, "Sesi dimulai untuk " + sesi.getPelanggan().getNama() + " di " + k.getNama()
                + " selama " + Tampilan.lamaDetik(sesi.getDurasiDetik())
                + ".\nSisa billing: " + Tampilan.lamaDetik(sesi.getPelanggan().getBillingDetik(k.getTipe())));
    }

    private void selesaikan() throws WarnetException {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= berjalan.size()) {
            throw new DataTidakValidException("Pilih sesi yang akan diselesaikan pada tabel");
        }
        Sesi s = berjalan.get(baris);
        LocalDateTime sekarang = LocalDateTime.now();
        s.selesai(sekarang);
        String info = "Akhiri sesi " + s.getPelanggan().getNama() + " di " + s.getKomputer().getNama() + "?";
        if (s.getBillingKembaliDetik() > 0) {
            info += "\nSisa " + Tampilan.lamaDetik(s.getBillingKembaliDetik())
                    + " akan dikembalikan ke billing " + s.getKomputer().getTipe() + ".";
        }
        if (!Tampilan.konfirmasi(this, info)) {
            return;
        }
        sesiDao.selesaikan(s);
        muatUlang();
        Tampilan.info(this, "Sesi selesai. Komputer " + s.getKomputer().getNama() + " kini TERSEDIA."
                + (s.getBillingKembaliDetik() > 0
                ? "\nSisa " + Tampilan.lamaDetik(s.getBillingKembaliDetik()) + " sudah dikembalikan ke billing."
                : ""));
    }

    private void perbaruiWaktu() {
        LocalDateTime sekarang = LocalDateTime.now();
        List<String> habis = new ArrayList<>();
        for (int i = 0; i < berjalan.size(); i++) {
            Sesi s = berjalan.get(i);
            String teks = hitungSisaTeks(s, sekarang);
            if (i < model.getRowCount()) {
                model.setValueAt(teks, i, KOLOM_SISA);
            }
            if (TEKS_HABIS.equals(teks)) {
                habis.add(s.getKomputer().getNama());
            }
        }
        if (habis.isEmpty()) {
            lblPeringatan.setText(" ");
        } else {
            lblPeringatan.setText("Waktu habis: " + String.join(", ", habis)
                    + ". Klik Selesaikan Sesi setelah pelanggan selesai.");
        }
    }

    private static String hitungSisaTeks(Sesi s, LocalDateTime sekarang) {
        long sisa = s.hitungSisaDetik(sekarang);
        return sisa == 0 ? TEKS_HABIS : Tampilan.jamMenitDetik(java.time.Duration.ofSeconds(sisa));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMulai = new javax.swing.JPanel();
        lblIdPelanggan = new javax.swing.JLabel();
        pnlId = new javax.swing.JPanel();
        txtPelanggan = new javax.swing.JTextField();
        btnCek = new javax.swing.JButton();
        lblKomputer = new javax.swing.JLabel();
        cmbKomputer = new javax.swing.JComboBox<>();
        btnMulai = new javax.swing.JButton();
        lblLamaMain = new javax.swing.JLabel();
        pnlLama = new javax.swing.JPanel();
        spnJam = new javax.swing.JSpinner();
        lblJam = new javax.swing.JLabel();
        spnMenit = new javax.swing.JSpinner();
        lblMenit = new javax.swing.JLabel();
        btnPakaiSemua = new javax.swing.JButton();
        lblPelanggan = new javax.swing.JLabel();
        lblTersedia = new javax.swing.JLabel();
        pnlBerjalan = new javax.swing.JPanel();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlBawah = new javax.swing.JPanel();
        pnlTombol = new javax.swing.JPanel();
        btnSelesai = new javax.swing.JButton();
        btnSegarkan = new javax.swing.JButton();
        lblPetunjuk = new javax.swing.JLabel();
        lblPeringatan = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlMulai.setBorder(javax.swing.BorderFactory.createTitledBorder("Mulai sesi baru (pakai billing pelanggan)"));
        pnlMulai.setLayout(new java.awt.GridBagLayout());

        lblIdPelanggan.setText("ID pelanggan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(lblIdPelanggan, gridBagConstraints);

        pnlId.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        txtPelanggan.setColumns(10);
        txtPelanggan.addActionListener(this::txtPelangganActionPerformed);
        pnlId.add(txtPelanggan);

        btnCek.setText("Cek");
        btnCek.setFocusPainted(false);
        btnCek.setPreferredSize(new java.awt.Dimension(100, 32));
        btnCek.addActionListener(this::btnCekActionPerformed);
        pnlId.add(btnCek);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(pnlId, gridBagConstraints);

        lblKomputer.setText("Komputer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(lblKomputer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(cmbKomputer, gridBagConstraints);

        btnMulai.setText("Mulai Sesi");
        btnMulai.setFocusPainted(false);
        btnMulai.setPreferredSize(new java.awt.Dimension(110, 32));
        btnMulai.addActionListener(this::btnMulaiActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(btnMulai, gridBagConstraints);

        lblLamaMain.setText("Lama main");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(lblLamaMain, gridBagConstraints);

        pnlLama.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        spnJam.setModel(new javax.swing.SpinnerNumberModel(1, 0, 24, 1));
        pnlLama.add(spnJam);

        lblJam.setText("jam");
        pnlLama.add(lblJam);

        spnMenit.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 5));
        pnlLama.add(spnMenit);

        lblMenit.setText("menit");
        pnlLama.add(lblMenit);

        btnPakaiSemua.setText("Pakai semua");
        btnPakaiSemua.setFocusPainted(false);
        btnPakaiSemua.setPreferredSize(new java.awt.Dimension(110, 32));
        btnPakaiSemua.addActionListener(this::btnPakaiSemuaActionPerformed);
        pnlLama.add(btnPakaiSemua);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(pnlLama, gridBagConstraints);

        lblPelanggan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPelanggan.setText("Masukkan ID pelanggan lalu tekan Cek");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(lblPelanggan, gridBagConstraints);

        lblTersedia.setForeground(new java.awt.Color(95, 107, 122));
        lblTersedia.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlMulai.add(lblTersedia, gridBagConstraints);

        add(pnlMulai, java.awt.BorderLayout.NORTH);

        pnlBerjalan.setBorder(javax.swing.BorderFactory.createTitledBorder("Sesi berjalan"));
        pnlBerjalan.setLayout(new java.awt.BorderLayout());

        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Komputer", "Pelanggan", "Mulai", "Berakhir", "Sisa waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);

        pnlBerjalan.add(spTabel, java.awt.BorderLayout.CENTER);

        pnlBawah.setLayout(new java.awt.BorderLayout());

        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 4));

        btnSelesai.setText("Selesaikan Sesi");
        btnSelesai.setFocusPainted(false);
        btnSelesai.setPreferredSize(new java.awt.Dimension(130, 32));
        btnSelesai.addActionListener(this::btnSelesaiActionPerformed);
        pnlTombol.add(btnSelesai);

        btnSegarkan.setText("Segarkan");
        btnSegarkan.setFocusPainted(false);
        btnSegarkan.setPreferredSize(new java.awt.Dimension(100, 32));
        btnSegarkan.addActionListener(this::btnSegarkanActionPerformed);
        pnlTombol.add(btnSegarkan);

        lblPetunjuk.setForeground(new java.awt.Color(95, 107, 122));
        lblPetunjuk.setText("Sisa waktu saat sesi diakhiri dikembalikan ke billing pelanggan");
        pnlTombol.add(lblPetunjuk);

        pnlBawah.add(pnlTombol, java.awt.BorderLayout.NORTH);

        lblPeringatan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPeringatan.setForeground(new java.awt.Color(179, 38, 30));
        lblPeringatan.setText(" ");
        pnlBawah.add(lblPeringatan, java.awt.BorderLayout.SOUTH);

        pnlBerjalan.add(pnlBawah, java.awt.BorderLayout.SOUTH);

        add(pnlBerjalan, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCekActionPerformed
        Tampilan.coba(this, this::cekPelanggan);
    }//GEN-LAST:event_btnCekActionPerformed

    private void txtPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPelangganActionPerformed
        Tampilan.coba(this, this::cekPelanggan);
    }//GEN-LAST:event_txtPelangganActionPerformed

    private void btnMulaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMulaiActionPerformed
        Tampilan.coba(this, this::mulai);
    }//GEN-LAST:event_btnMulaiActionPerformed

    private void btnPakaiSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPakaiSemuaActionPerformed
        pakaiSemuaBilling();
    }//GEN-LAST:event_btnPakaiSemuaActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        Tampilan.coba(this, this::selesaikan);
    }//GEN-LAST:event_btnSelesaiActionPerformed

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muatUlang();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCek;
    private javax.swing.JButton btnMulai;
    private javax.swing.JButton btnPakaiSemua;
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JButton btnSelesai;
    private javax.swing.JComboBox<latihan.warnetku.model.Komputer> cmbKomputer;
    private javax.swing.JLabel lblIdPelanggan;
    private javax.swing.JLabel lblJam;
    private javax.swing.JLabel lblKomputer;
    private javax.swing.JLabel lblLamaMain;
    private javax.swing.JLabel lblMenit;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPeringatan;
    private javax.swing.JLabel lblPetunjuk;
    private javax.swing.JLabel lblTersedia;
    private javax.swing.JPanel pnlBawah;
    private javax.swing.JPanel pnlBerjalan;
    private javax.swing.JPanel pnlId;
    private javax.swing.JPanel pnlLama;
    private javax.swing.JPanel pnlMulai;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JSpinner spnJam;
    private javax.swing.JSpinner spnMenit;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtPelanggan;
    // End of variables declaration//GEN-END:variables
}
