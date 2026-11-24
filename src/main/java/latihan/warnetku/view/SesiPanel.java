package latihan.warnetku.view;

import latihan.warnetku.dao.KomputerDao;
import latihan.warnetku.dao.KomputerDaoJdbc;
import latihan.warnetku.dao.SesiDao;
import latihan.warnetku.dao.SesiDaoJdbc;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pengguna;
import latihan.warnetku.model.Sesi;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SesiPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private static final int KOLOM_BERJALAN = 4;
    private static final int KOLOM_PERKIRAAN = 5;

    private final transient Pengguna operator;
    private final transient KomputerDao komputerDao = new KomputerDaoJdbc();
    private final transient SesiDao sesiDao = new SesiDaoJdbc();
    private transient List<Sesi> berjalan = new ArrayList<>();
    private final DefaultTableModel model;
    private final Timer pencacah = new Timer(1000, e -> perbaruiWaktu());

    public SesiPanel(Pengguna operator) {
        this.operator = operator;
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        ((java.awt.BorderLayout) pnlBerjalan.getLayout()).setVgap(8);
        ((FlowLayout) pnlMulai.getLayout()).setHgap(10);
        ((FlowLayout) pnlMulai.getLayout()).setVgap(8);
        ((FlowLayout) pnlTombol.getLayout()).setHgap(10);
        ((FlowLayout) pnlTombol.getLayout()).setVgap(4);
        model = (DefaultTableModel) tabel.getModel();
        Tampilan.rapikanTabel(tabel, KOLOM_BERJALAN, KOLOM_PERKIRAAN);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(200);
        cmbKomputer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object nilai, int indeks,
                                                          boolean terpilih, boolean fokus) {
                super.getListCellRendererComponent(list, nilai, indeks, terpilih, fokus);
                if (nilai instanceof Komputer) {
                    Komputer k = (Komputer) nilai;
                    setText(k.getNama() + "  |  " + k.getTipe() + "  |  " + Tampilan.rupiah(k.getTarifPerJam()) + "/jam");
                }
                return this;
            }
        });
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

            cmbKomputer.setModel(new DefaultComboBoxModel<>(tersedia.toArray(new Komputer[0])));
            btnMulai.setEnabled(!tersedia.isEmpty());
            cmbKomputer.setEnabled(!tersedia.isEmpty());
            lblTersedia.setText(tersedia.isEmpty() ? "Tidak ada komputer tersedia"
                    : tersedia.size() + " komputer tersedia");

            int terpilih = tabel.getSelectedRow();
            model.setRowCount(0);
            for (Sesi s : berjalan) {
                model.addRow(new Object[]{s.getKode(), s.getKomputer().getNama(), s.getNamaPelanggan(),
                        Tampilan.jam(s.getWaktuMulai()), "", ""});
            }
            if (terpilih >= 0 && terpilih < berjalan.size()) {
                tabel.setRowSelectionInterval(terpilih, terpilih);
            }
            perbaruiWaktu();
        });
    }

    private void perbaruiWaktu() {
        LocalDateTime sekarang = LocalDateTime.now();
        for (int i = 0; i < berjalan.size() && i < model.getRowCount(); i++) {
            Sesi s = berjalan.get(i);
            model.setValueAt(Tampilan.jamMenitDetik(Duration.between(s.getWaktuMulai(), sekarang)), i, KOLOM_BERJALAN);
            model.setValueAt(Tampilan.rupiah(s.hitungTotal()), i, KOLOM_PERKIRAAN);
        }
    }

    private void mulai() throws WarnetException {
        Komputer komputer = (Komputer) cmbKomputer.getSelectedItem();
        if (komputer == null) {
            throw new DataTidakValidException("Tidak ada komputer tersedia untuk dipakai");
        }
        Sesi sesi = new Sesi(komputer, operator, txtPelanggan.getText());
        try {
            sesiDao.mulai(sesi);
        } finally {
            muatUlang();
        }
        txtPelanggan.setText("");
        Tampilan.info(this, "Sesi dimulai di " + komputer.getNama() + " untuk " + sesi.getNamaPelanggan() + ".");
    }

    private void selesaiDanBayar() {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= berjalan.size()) {
            Tampilan.error(this, "Pilih sesi yang berjalan pada tabel terlebih dahulu");
            return;
        }
        Sesi sesi = berjalan.get(baris);
        sesi.selesai(LocalDateTime.now());
        BayarDialog dialog = new BayarDialog(SwingUtilities.getWindowAncestor(this), sesi, sesiDao);
        dialog.setVisible(true);
        muatUlang();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMulai = new javax.swing.JPanel();
        lblKomputer = new javax.swing.JLabel();
        cmbKomputer = new javax.swing.JComboBox<>();
        lblPelanggan = new javax.swing.JLabel();
        txtPelanggan = new javax.swing.JTextField();
        btnMulai = new javax.swing.JButton();
        lblTersedia = new javax.swing.JLabel();
        pnlBerjalan = new javax.swing.JPanel();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlTombol = new javax.swing.JPanel();
        btnSelesai = new javax.swing.JButton();
        btnSegarkan = new javax.swing.JButton();
        lblPetunjuk = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlMulai.setBorder(javax.swing.BorderFactory.createTitledBorder("Mulai sesi baru"));
        pnlMulai.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        lblKomputer.setText("Komputer");
        pnlMulai.add(lblKomputer);
        pnlMulai.add(cmbKomputer);
        lblPelanggan.setText("Nama pelanggan");
        pnlMulai.add(lblPelanggan);
        txtPelanggan.setColumns(18);
        txtPelanggan.addActionListener(this::txtPelangganActionPerformed);
        pnlMulai.add(txtPelanggan);
        btnMulai.setText("Mulai Sesi");
        btnMulai.setFocusPainted(false);
        btnMulai.setPreferredSize(new java.awt.Dimension(110, 32));
        btnMulai.addActionListener(this::btnMulaiActionPerformed);
        pnlMulai.add(btnMulai);
        lblTersedia.setForeground(new java.awt.Color(95, 107, 122));
        lblTersedia.setText(" ");
        pnlMulai.add(lblTersedia);
        add(pnlMulai, java.awt.BorderLayout.NORTH);
        pnlBerjalan.setBorder(javax.swing.BorderFactory.createTitledBorder("Sesi berjalan"));
        pnlBerjalan.setLayout(new java.awt.BorderLayout());
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Komputer", "Pelanggan", "Mulai", "Berjalan", "Perkiraan biaya"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);
        pnlBerjalan.add(spTabel, java.awt.BorderLayout.CENTER);
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnSelesai.setText("Selesai & Bayar");
        btnSelesai.setFocusPainted(false);
        btnSelesai.setPreferredSize(new java.awt.Dimension(140, 32));
        btnSelesai.addActionListener(this::btnSelesaiActionPerformed);
        pnlTombol.add(btnSelesai);
        btnSegarkan.setText("Segarkan");
        btnSegarkan.setFocusPainted(false);
        btnSegarkan.setPreferredSize(new java.awt.Dimension(100, 32));
        btnSegarkan.addActionListener(this::btnSegarkanActionPerformed);
        pnlTombol.add(btnSegarkan);
        lblPetunjuk.setForeground(new java.awt.Color(95, 107, 122));
        lblPetunjuk.setText("Biaya dibulatkan ke atas per 15 menit (minimal 15 menit)");
        pnlTombol.add(lblPetunjuk);
        pnlBerjalan.add(pnlTombol, java.awt.BorderLayout.SOUTH);
        add(pnlBerjalan, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPelangganActionPerformed
        btnMulai.doClick();
    }//GEN-LAST:event_txtPelangganActionPerformed

    private void btnMulaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMulaiActionPerformed
        Tampilan.coba(this, this::mulai);
    }//GEN-LAST:event_btnMulaiActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        selesaiDanBayar();
    }//GEN-LAST:event_btnSelesaiActionPerformed

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muatUlang();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMulai;
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JButton btnSelesai;
    private javax.swing.JComboBox<latihan.warnetku.model.Komputer> cmbKomputer;
    private javax.swing.JLabel lblKomputer;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPetunjuk;
    private javax.swing.JLabel lblTersedia;
    private javax.swing.JPanel pnlBerjalan;
    private javax.swing.JPanel pnlMulai;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtPelanggan;
    // End of variables declaration//GEN-END:variables
}
