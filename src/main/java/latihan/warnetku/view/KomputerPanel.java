package latihan.warnetku.view;

import latihan.warnetku.dao.KomputerDao;
import latihan.warnetku.dao.KomputerDaoJdbc;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.StatusKomputer;
import latihan.warnetku.model.TipeKomputer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class KomputerPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private final transient KomputerDao dao = new KomputerDaoJdbc();
    private transient List<Komputer> data = new ArrayList<>();
    private final DefaultTableModel model;

    public KomputerPanel() {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        model = (DefaultTableModel) tabel.getModel();
        cmbTipe.setModel(new DefaultComboBoxModel<>(TipeKomputer.values()));
        cmbStatus.setModel(new DefaultComboBoxModel<>(StatusKomputer.values()));
        Tampilan.rapikanTabel(tabel, 0, 3);
        tabel.getColumnModel().getColumn(4).setCellRenderer(new Tampilan.RenderTeks(SwingConstants.LEFT, nilai -> {
            if (nilai == StatusKomputer.TERSEDIA) {
                return Tampilan.HIJAU;
            }
            return nilai == StatusKomputer.DIPAKAI ? Tampilan.ORANYE : Tampilan.MERAH;
        }));
        tabel.getColumnModel().getColumn(0).setMaxWidth(70);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });
    }

    @Override
    public void muatUlang() {
        Komputer terpilih = komputerTerpilih();
        int idTerpilih = terpilih == null ? -1 : terpilih.getId();
        Tampilan.coba(this, () -> {
            data = dao.cariSemua();
            model.setRowCount(0);
            for (Komputer k : data) {
                model.addRow(new Object[]{k.getId(), k.getNama(), k.getTipe(),
                        Tampilan.rupiah(k.getTarifPerJam()), k.getStatus()});
            }
        });
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == idTerpilih) {
                tabel.setRowSelectionInterval(i, i);
                return;
            }
        }
        bersihkanForm();
    }

    private Komputer komputerTerpilih() {
        int baris = tabel.getSelectedRow();
        return (baris < 0 || baris >= data.size()) ? null : data.get(baris);
    }

    private void isiFormDariBaris() {
        Komputer k = komputerTerpilih();
        if (k == null) {
            return;
        }
        txtNama.setText(k.getNama());
        cmbTipe.setSelectedItem(k.getTipe());
        txtTarif.setText(String.valueOf(k.getTarifPerJam()));
        cmbStatus.setSelectedItem(k.getStatus());
        boolean dipakai = k.getStatus() == StatusKomputer.DIPAKAI;
        cmbStatus.setEnabled(!dipakai);
        lblCatatan.setText(dipakai ? "Komputer sedang dipakai: status diatur otomatis oleh sesi." : " ");
    }

    private void bersihkanForm() {
        tabel.clearSelection();
        txtNama.setText("");
        txtTarif.setText("");
        cmbTipe.setSelectedItem(TipeKomputer.REGULER);
        cmbStatus.setSelectedItem(StatusKomputer.TERSEDIA);
        cmbStatus.setEnabled(true);
        lblCatatan.setText(" ");
        txtNama.requestFocusInWindow();
    }

    private Komputer bacaForm(int id, StatusKomputer statusPaksa) throws WarnetException {
        int tarif = Tampilan.parseAngka(txtTarif.getText(), "Tarif per jam");
        StatusKomputer status = statusPaksa != null ? statusPaksa : (StatusKomputer) cmbStatus.getSelectedItem();
        if (statusPaksa == null && status == StatusKomputer.DIPAKAI) {
            throw new DataTidakValidException("Status DIPAKAI diatur otomatis saat sesi dimulai. "
                    + "Pilih TERSEDIA atau RUSAK.");
        }
        return new Komputer(id, txtNama.getText(), (TipeKomputer) cmbTipe.getSelectedItem(), tarif, status);
    }

    private void tambah() throws WarnetException {
        Komputer baru = bacaForm(0, null);
        dao.simpan(baru);
        bersihkanForm();
        muatUlang();
        Tampilan.info(this, "Komputer " + baru.getNama() + " ditambahkan.");
    }

    private void ubah() throws WarnetException {
        Komputer lama = komputerTerpilih();
        if (lama == null) {
            throw new DataTidakValidException("Pilih komputer pada tabel terlebih dahulu");
        }
        StatusKomputer paksa = lama.getStatus() == StatusKomputer.DIPAKAI ? StatusKomputer.DIPAKAI : null;
        Komputer baru = bacaForm(lama.getId(), paksa);
        dao.ubah(baru);
        muatUlang();
        Tampilan.info(this, "Komputer " + baru.getNama() + " diperbarui.");
    }

    private void hapus() throws WarnetException {
        Komputer k = komputerTerpilih();
        if (k == null) {
            throw new DataTidakValidException("Pilih komputer pada tabel terlebih dahulu");
        }
        if (k.getStatus() == StatusKomputer.DIPAKAI) {
            throw new DataTidakValidException("Komputer sedang dipakai, selesaikan sesinya dulu");
        }
        if (!Tampilan.konfirmasi(this, "Hapus komputer " + k.getNama() + "?")) {
            return;
        }
        dao.hapus(k.getId());
        bersihkanForm();
        muatUlang();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblPetunjuk = new javax.swing.JLabel();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlForm = new javax.swing.JPanel();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblTipe = new javax.swing.JLabel();
        cmbTipe = new javax.swing.JComboBox<>();
        lblTarif = new javax.swing.JLabel();
        txtTarif = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        pnlTombol = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        lblCatatan = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlAtas.setLayout(new java.awt.BorderLayout());
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblJudul.setText("Data Komputer");
        pnlAtas.add(lblJudul, java.awt.BorderLayout.WEST);
        lblPetunjuk.setForeground(new java.awt.Color(95, 107, 122));
        lblPetunjuk.setText("Pilih baris untuk mengubah atau menghapus");
        pnlAtas.add(lblPetunjuk, java.awt.BorderLayout.EAST);
        add(pnlAtas, java.awt.BorderLayout.NORTH);
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Tipe", "Tarif / jam", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);
        add(spTabel, java.awt.BorderLayout.CENTER);
        pnlForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Form komputer"));
        pnlForm.setLayout(new java.awt.GridBagLayout());
        lblNama.setText("Nama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblNama, gridBagConstraints);
        txtNama.setColumns(14);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(txtNama, gridBagConstraints);
        lblTipe.setText("Tipe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblTipe, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(cmbTipe, gridBagConstraints);
        lblTarif.setText("Tarif per jam (Rp)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblTarif, gridBagConstraints);
        txtTarif.setColumns(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(txtTarif, gridBagConstraints);
        lblStatus.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblStatus, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(cmbStatus, gridBagConstraints);
        btnTambah.setText("Tambah");
        btnTambah.setFocusPainted(false);
        btnTambah.setPreferredSize(new java.awt.Dimension(100, 32));
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlTombol.add(btnTambah);
        btnUbah.setText("Ubah");
        btnUbah.setFocusPainted(false);
        btnUbah.setPreferredSize(new java.awt.Dimension(100, 32));
        btnUbah.addActionListener(this::btnUbahActionPerformed);
        pnlTombol.add(btnUbah);
        btnHapus.setText("Hapus");
        btnHapus.setFocusPainted(false);
        btnHapus.setPreferredSize(new java.awt.Dimension(100, 32));
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        pnlTombol.add(btnHapus);
        btnBaru.setText("Bersihkan");
        btnBaru.setFocusPainted(false);
        btnBaru.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBaru.addActionListener(this::btnBaruActionPerformed);
        pnlTombol.add(btnBaru);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(pnlTombol, gridBagConstraints);
        lblCatatan.setForeground(new java.awt.Color(95, 107, 122));
        lblCatatan.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblCatatan, gridBagConstraints);
        add(pnlForm, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        Tampilan.coba(this, this::tambah);
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        Tampilan.coba(this, this::ubah);
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        Tampilan.coba(this, this::hapus);
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        bersihkanForm();
    }//GEN-LAST:event_btnBaruActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<latihan.warnetku.model.StatusKomputer> cmbStatus;
    private javax.swing.JComboBox<latihan.warnetku.model.TipeKomputer> cmbTipe;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblPetunjuk;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTarif;
    private javax.swing.JLabel lblTipe;
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTarif;
    // End of variables declaration//GEN-END:variables
}
