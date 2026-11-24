package latihan.warnetku.view;

import latihan.warnetku.dao.IsiBillingDao;
import latihan.warnetku.dao.IsiBillingDaoJdbc;
import latihan.warnetku.dao.KomputerDao;
import latihan.warnetku.dao.KomputerDaoJdbc;
import latihan.warnetku.dao.PelangganDao;
import latihan.warnetku.dao.PelangganDaoJdbc;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pelanggan;
import latihan.warnetku.model.Pengguna;
import latihan.warnetku.model.StatusKomputer;
import latihan.warnetku.model.TipeKomputer;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PelangganPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private static final long serialVersionUID = 1L;

    private final transient Pengguna petugas;
    private final transient PelangganDao dao = new PelangganDaoJdbc();
    private final transient IsiBillingDao isiDao = new IsiBillingDaoJdbc();
    private final transient KomputerDao komputerDao = new KomputerDaoJdbc();
    private transient List<Pelanggan> data = new ArrayList<>();
    private final DefaultTableModel model;

    public PelangganPanel(Pengguna petugas) {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        this.petugas = petugas;
        model = (DefaultTableModel) tabel.getModel();

        Tampilan.rapikanTabel(tabel, 2, 3);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Pelanggan p = terpilih();
                if (p != null) {
                    txtNama.setText(p.getNama());
                }
            }
        });
    }

    @Override
    public void muatUlang() {
        Pelanggan sebelum = terpilih();
        int idTerpilih = sebelum == null ? -1 : sebelum.getId();
        Tampilan.coba(this, () -> {
            data = dao.cariSemua();
            model.setRowCount(0);
            for (Pelanggan p : data) {
                model.addRow(new Object[]{p.getKode(), p.getNama(),
                        Tampilan.lamaDetik(p.getBillingRegulerDetik()),
                        Tampilan.lamaDetik(p.getBillingVipDetik()),
                        Tampilan.waktu(p.getDibuat())});
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

    private Pelanggan terpilih() {
        int baris = tabel.getSelectedRow();
        return (baris < 0 || baris >= data.size()) ? null : data.get(baris);
    }

    private void bersihkanForm() {
        tabel.clearSelection();
        txtNama.setText("");
        txtNama.requestFocusInWindow();
    }

    private void tambah() throws WarnetException {
        Pelanggan p = new Pelanggan(txtNama.getText());
        dao.simpan(p);
        muatUlang();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == p.getId()) {
                tabel.setRowSelectionInterval(i, i);
                break;
            }
        }
        lblCatatan.setText("ID pelanggan baru: " + p.getKode() + " (" + p.getNama() + ")");
        Tampilan.info(this, "Pelanggan baru tersimpan.\nID: " + p.getKode()
                + "\nBeri tahu ID ini kepada pelanggan untuk digunakan saat isi billing dan mulai sesi.");
    }

    private void ubah() throws WarnetException {
        Pelanggan p = terpilih();
        if (p == null) {
            throw new DataTidakValidException("Pilih pelanggan pada tabel terlebih dahulu");
        }
        p.setNama(txtNama.getText());
        dao.ubah(p);
        muatUlang();
        Tampilan.info(this, "Nama pelanggan " + p.getKode() + " diperbarui.");
    }

    private void hapus() throws WarnetException {
        Pelanggan p = terpilih();
        if (p == null) {
            throw new DataTidakValidException("Pilih pelanggan pada tabel terlebih dahulu");
        }
        if (!Tampilan.konfirmasi(this, "Hapus pelanggan " + p + "?")) {
            return;
        }
        dao.hapus(p.getId());
        bersihkanForm();
        muatUlang();
    }

    private void isiBilling() throws WarnetException {
        Pelanggan p = terpilih();
        if (p == null) {
            throw new DataTidakValidException("Pilih pelanggan pada tabel terlebih dahulu");
        }
        Map<TipeKomputer, Komputer> perTipe = new LinkedHashMap<>();
        for (Komputer k : komputerDao.cariSemua()) {
            if (k.getStatus() != StatusKomputer.RUSAK) {
                perTipe.putIfAbsent(k.getTipe(), k);
            }
        }
        for (Komputer k : komputerDao.cariSemua()) {
            perTipe.putIfAbsent(k.getTipe(), k);
        }
        if (perTipe.isEmpty()) {
            throw new DataTidakValidException("Belum ada data komputer untuk acuan tarif");
        }
        List<Komputer> acuan = new ArrayList<>(perTipe.values());
        acuan.sort(Comparator.comparing(Komputer::getTipe));
        IsiBillingDialog dialog = new IsiBillingDialog(SwingUtilities.getWindowAncestor(this), p, petugas,
                acuan, isiDao);
        dialog.setVisible(true);
        muatUlang();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblKeterangan = new javax.swing.JLabel();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlForm = new javax.swing.JPanel();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        pnlTombol = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnIsiBilling = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        lblCatatan = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlAtas.setLayout(new java.awt.BorderLayout());

        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblJudul.setText("Data Pelanggan");
        pnlAtas.add(lblJudul, java.awt.BorderLayout.WEST);

        lblKeterangan.setForeground(new java.awt.Color(95, 107, 122));
        lblKeterangan.setText("ID pelanggan dibuat otomatis. Sisa billing tersimpan di ID-nya.");
        pnlAtas.add(lblKeterangan, java.awt.BorderLayout.EAST);

        add(pnlAtas, java.awt.BorderLayout.NORTH);

        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pelanggan", "Nama", "Billing REGULER", "Billing VIP", "Terdaftar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);

        add(spTabel, java.awt.BorderLayout.CENTER);

        pnlForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Form pelanggan"));
        pnlForm.setLayout(new java.awt.GridBagLayout());

        lblNama.setText("Nama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(lblNama, gridBagConstraints);

        txtNama.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(txtNama, gridBagConstraints);

        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 0));

        btnTambah.setText("Buat ID Baru");
        btnTambah.setFocusPainted(false);
        btnTambah.setPreferredSize(new java.awt.Dimension(110, 32));
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlTombol.add(btnTambah);

        btnUbah.setText("Ubah Nama");
        btnUbah.setFocusPainted(false);
        btnUbah.setPreferredSize(new java.awt.Dimension(110, 32));
        btnUbah.addActionListener(this::btnUbahActionPerformed);
        pnlTombol.add(btnUbah);

        btnHapus.setText("Hapus");
        btnHapus.setFocusPainted(false);
        btnHapus.setPreferredSize(new java.awt.Dimension(100, 32));
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        pnlTombol.add(btnHapus);

        btnIsiBilling.setText("Isi Billing");
        btnIsiBilling.setFocusPainted(false);
        btnIsiBilling.setPreferredSize(new java.awt.Dimension(100, 32));
        btnIsiBilling.addActionListener(this::btnIsiBillingActionPerformed);
        pnlTombol.add(btnIsiBilling);

        btnBersihkan.setText("Bersihkan");
        btnBersihkan.setFocusPainted(false);
        btnBersihkan.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBersihkan.addActionListener(this::btnBersihkanActionPerformed);
        pnlTombol.add(btnBersihkan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        pnlForm.add(pnlTombol, gridBagConstraints);

        lblCatatan.setForeground(new java.awt.Color(95, 107, 122));
        lblCatatan.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
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

    private void btnIsiBillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIsiBillingActionPerformed
        Tampilan.coba(this, this::isiBilling);
    }//GEN-LAST:event_btnIsiBillingActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        bersihkanForm();
    }//GEN-LAST:event_btnBersihkanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnIsiBilling;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblKeterangan;
    private javax.swing.JLabel lblNama;
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
