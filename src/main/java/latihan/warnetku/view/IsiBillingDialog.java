package latihan.warnetku.view;

import latihan.warnetku.dao.IsiBillingDao;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.IsiBilling;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pelanggan;
import latihan.warnetku.model.Pengguna;
import latihan.warnetku.model.StatusKomputer;
import latihan.warnetku.model.TipeKomputer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IsiBillingDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private final transient Pelanggan pelanggan;
    private final transient Pengguna petugas;
    private final transient IsiBillingDao dao;
    private boolean berhasil;

    public IsiBillingDialog(Window induk, Pelanggan pelanggan, Pengguna petugas, List<Komputer> komputer,
                            IsiBillingDao dao) {
        super(induk, "Isi Billing", ModalityType.APPLICATION_MODAL);
        initComponents();
        this.pelanggan = pelanggan;
        this.petugas = petugas;
        this.dao = dao;

        lblPelanggan.setText(pelanggan.getKode() + " - " + pelanggan.getNama());

        Map<TipeKomputer, Komputer> perTipe = new LinkedHashMap<>();
        for (Komputer k : komputer) {
            if (k.getStatus() != StatusKomputer.RUSAK) {
                perTipe.putIfAbsent(k.getTipe(), k);
            }
        }
        for (Komputer k : komputer) {
            perTipe.putIfAbsent(k.getTipe(), k);
        }
        List<Komputer> daftarOpsi = new ArrayList<>(perTipe.values());
        daftarOpsi.sort(Comparator.comparing(Komputer::getTipe));
        cmbAcuan.setModel(new javax.swing.DefaultComboBoxModel<>(daftarOpsi.toArray(new Komputer[0])));

        cmbAcuan.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object nilai, int indeks,
                                                          boolean terpilih, boolean fokus) {
                super.getListCellRendererComponent(list, nilai, indeks, terpilih, fokus);
                if (nilai instanceof Komputer) {
                    Komputer k = (Komputer) nilai;
                    setText(k.getTipe() + "  |  " + Tampilan.rupiah(k.getTarifPerJam()) + "/jam");
                }
                return this;
            }
        });

        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                perbaruiTotalDanKembalian();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                perbaruiTotalDanKembalian();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                perbaruiTotalDanKembalian();
            }
        };
        txtBayar.getDocument().addDocumentListener(dl);
        cmbAcuan.addActionListener(e -> perbaruiTotalDanKembalian());
        spnJam.addChangeListener(e -> perbaruiTotalDanKembalian());
        getRootPane().setDefaultButton(btnBayar);

        perbaruiTotalDanKembalian();
        pack();
        setLocationRelativeTo(induk);
        SwingUtilities.invokeLater(txtBayar::requestFocusInWindow);
    }

    boolean isBerhasil() {
        return berhasil;
    }

    private int jam() throws DataTidakValidException {
        try {
            spnJam.commitEdit();
        } catch (ParseException e) {
            throw new DataTidakValidException("Jumlah jam harus berupa angka bulat 1 sampai " + IsiBilling.JAM_MAKS);
        }
        return ((Number) spnJam.getValue()).intValue();
    }

    private int total() throws DataTidakValidException {
        Komputer k = (Komputer) cmbAcuan.getSelectedItem();
        if (k == null) {
            throw new DataTidakValidException("Jenis billing wajib dipilih");
        }
        return new IsiBilling(pelanggan, petugas, k, jam()).getTotal();
    }

    private void perbaruiTotalDanKembalian() {
        try {
            int total = total();
            lblTotal.setText(Tampilan.rupiah(total));
            int selisih = Tampilan.parseAngka(txtBayar.getText(), "Uang bayar") - total;
            if (selisih >= 0) {
                lblKembalian.setText(Tampilan.rupiah(selisih));
                lblKembalian.setForeground(Tampilan.HIJAU);
            } else {
                lblKembalian.setText("Kurang " + Tampilan.rupiah(-selisih));
                lblKembalian.setForeground(Tampilan.MERAH);
            }
        } catch (DataTidakValidException e) {
            lblKembalian.setText("-");
            lblKembalian.setForeground(Tampilan.ABU);
        }
    }

    private void bayar() throws WarnetException {
        Komputer k = (Komputer) cmbAcuan.getSelectedItem();
        if (k == null) {
            throw new DataTidakValidException("Jenis billing wajib dipilih");
        }
        IsiBilling isi = new IsiBilling(pelanggan, petugas, k, jam());
        int uang = Tampilan.parseAngka(txtBayar.getText(), "Uang bayar");
        dao.isi(isi, uang);
        berhasil = true;
        Tampilan.info(this, "Billing " + isi.getTipe() + " bertambah " + Tampilan.lama(isi.getDurasiJam())
                + " untuk " + pelanggan.getNama() + ".\nSisa billing sekarang: "
                + Tampilan.lamaDetik(pelanggan.getBillingDetik(isi.getTipe()))
                + "\nKembalian: " + Tampilan.rupiah(isi.getKembalian()));
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlIsi = new javax.swing.JPanel();
        lblPelangganCap = new javax.swing.JLabel();
        lblPelanggan = new javax.swing.JLabel();
        lblBillingUntukCap = new javax.swing.JLabel();
        cmbAcuan = new javax.swing.JComboBox<>();
        lblTambahJamCap = new javax.swing.JLabel();
        spnJam = new javax.swing.JSpinner();
        lblTotalCap = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblBayarCap = new javax.swing.JLabel();
        pnlBayar = new javax.swing.JPanel();
        txtBayar = new javax.swing.JTextField();
        btnPas = new javax.swing.JButton();
        lblKembalianCap = new javax.swing.JLabel();
        lblKembalian = new javax.swing.JLabel();
        pnlTombol = new javax.swing.JPanel();
        btnBatal = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Isi Billing");
        setModal(true);
        setResizable(false);

        pnlIsi.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 20, 8, 20));
        pnlIsi.setLayout(new java.awt.GridBagLayout());

        lblPelangganCap.setForeground(new java.awt.Color(95, 107, 122));
        lblPelangganCap.setText("Pelanggan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblPelangganCap, gridBagConstraints);

        lblPelanggan.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblPelanggan, gridBagConstraints);

        lblBillingUntukCap.setForeground(new java.awt.Color(95, 107, 122));
        lblBillingUntukCap.setText("Billing untuk");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblBillingUntukCap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(cmbAcuan, gridBagConstraints);

        lblTambahJamCap.setForeground(new java.awt.Color(95, 107, 122));
        lblTambahJamCap.setText("Tambah billing (jam)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTambahJamCap, gridBagConstraints);

        spnJam.setModel(new javax.swing.SpinnerNumberModel(1, 1, 24, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(spnJam, gridBagConstraints);

        lblTotalCap.setForeground(new java.awt.Color(95, 107, 122));
        lblTotalCap.setText("Total tagihan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTotalCap, gridBagConstraints);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(31, 78, 140));
        lblTotal.setText("Rp 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTotal, gridBagConstraints);

        lblBayarCap.setForeground(new java.awt.Color(95, 107, 122));
        lblBayarCap.setText("Uang bayar (Rp)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblBayarCap, gridBagConstraints);

        pnlBayar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        txtBayar.setColumns(12);
        pnlBayar.add(txtBayar);

        btnPas.setText("Uang pas");
        btnPas.setFocusPainted(false);
        btnPas.setPreferredSize(new java.awt.Dimension(100, 32));
        btnPas.addActionListener(this::btnPasActionPerformed);
        pnlBayar.add(btnPas);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(pnlBayar, gridBagConstraints);

        lblKembalianCap.setForeground(new java.awt.Color(95, 107, 122));
        lblKembalianCap.setText("Kembalian");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKembalianCap, gridBagConstraints);

        lblKembalian.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKembalian.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKembalian, gridBagConstraints);

        getContentPane().add(pnlIsi, java.awt.BorderLayout.CENTER);

        pnlTombol.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 8, 12));
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 8));

        btnBatal.setText("Batal");
        btnBatal.setFocusPainted(false);
        btnBatal.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        pnlTombol.add(btnBatal);

        btnBayar.setText("Bayar & Isi");
        btnBayar.setFocusPainted(false);
        btnBayar.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBayar.addActionListener(this::btnBayarActionPerformed);
        pnlTombol.add(btnBayar);

        getContentPane().add(pnlTombol, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasActionPerformed
        try {
            txtBayar.setText(String.valueOf(total()));
        } catch (WarnetException ex) {
            Tampilan.error(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnPasActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        Tampilan.coba(this, this::bayar);
    }//GEN-LAST:event_btnBayarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnPas;
    private javax.swing.JComboBox<latihan.warnetku.model.Komputer> cmbAcuan;
    private javax.swing.JLabel lblBayarCap;
    private javax.swing.JLabel lblBillingUntukCap;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblKembalianCap;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPelangganCap;
    private javax.swing.JLabel lblTambahJamCap;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalCap;
    private javax.swing.JPanel pnlBayar;
    private javax.swing.JPanel pnlIsi;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JSpinner spnJam;
    private javax.swing.JTextField txtBayar;
    // End of variables declaration//GEN-END:variables
}
