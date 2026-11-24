package latihan.warnetku.view;

import latihan.warnetku.dao.SesiDao;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Sesi;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;
import java.awt.Window;

final class BayarDialog extends javax.swing.JDialog {

    private final transient Sesi sesi;
    private final transient SesiDao dao;

    BayarDialog(Window induk, Sesi sesi, SesiDao dao) {
        super(induk, ModalityType.APPLICATION_MODAL);
        this.sesi = sesi;
        this.dao = dao;
        initComponents();
        ((FlowLayout) pnlBayar.getLayout()).setHgap(6);
        ((FlowLayout) pnlBayar.getLayout()).setVgap(0);
        ((FlowLayout) pnlTombol.getLayout()).setHgap(8);
        ((FlowLayout) pnlTombol.getLayout()).setVgap(8);
        isiData();
        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                perbaruiKembalian();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                perbaruiKembalian();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                perbaruiKembalian();
            }
        });
        getRootPane().setDefaultButton(btnBayar);
        pack();
        setLocationRelativeTo(induk);
        SwingUtilities.invokeLater(txtBayar::requestFocusInWindow);
    }

    private void isiData() {
        lblKode.setText(sesi.getKode());
        lblKomputer.setText(sesi.getKomputer().getNama() + " (" + sesi.getKomputer().getTipe() + ")");
        lblPelanggan.setText(sesi.getNamaPelanggan());
        lblMulai.setText(Tampilan.waktu(sesi.getWaktuMulai()));
        lblSelesai.setText(Tampilan.waktu(sesi.getWaktuSelesai()));
        lblDurasi.setText(Tampilan.durasi(sesi.getDurasiMenit()));
        lblTarif.setText(Tampilan.rupiah(sesi.getTarifPerJam()));
        lblTotal.setText(Tampilan.rupiah(sesi.getTotal()));
    }

    private void perbaruiKembalian() {
        try {
            int uang = Tampilan.parseAngka(txtBayar.getText(), "Uang bayar");
            int selisih = uang - sesi.getTotal();
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
        int uang = Tampilan.parseAngka(txtBayar.getText(), "Uang bayar");
        dao.selesaikan(sesi, uang);
        Tampilan.info(this, "Pembayaran berhasil.\nKembalian: " + Tampilan.rupiah(sesi.getKembalian()));
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlIsi = new javax.swing.JPanel();
        lblKodeCap = new javax.swing.JLabel();
        lblKode = new javax.swing.JLabel();
        lblKomputerCap = new javax.swing.JLabel();
        lblKomputer = new javax.swing.JLabel();
        lblPelangganCap = new javax.swing.JLabel();
        lblPelanggan = new javax.swing.JLabel();
        lblMulaiCap = new javax.swing.JLabel();
        lblMulai = new javax.swing.JLabel();
        lblSelesaiCap = new javax.swing.JLabel();
        lblSelesai = new javax.swing.JLabel();
        lblDurasiCap = new javax.swing.JLabel();
        lblDurasi = new javax.swing.JLabel();
        lblTarifCap = new javax.swing.JLabel();
        lblTarif = new javax.swing.JLabel();
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
        setTitle("Selesai & Bayar");
        setResizable(false);

        pnlIsi.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 20, 8, 20));
        pnlIsi.setLayout(new java.awt.GridBagLayout());
        lblKodeCap.setForeground(new java.awt.Color(95, 107, 122));
        lblKodeCap.setText("Kode sesi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKodeCap, gridBagConstraints);
        lblKode.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKode, gridBagConstraints);
        lblKomputerCap.setForeground(new java.awt.Color(95, 107, 122));
        lblKomputerCap.setText("Komputer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKomputerCap, gridBagConstraints);
        lblKomputer.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKomputer, gridBagConstraints);
        lblPelangganCap.setForeground(new java.awt.Color(95, 107, 122));
        lblPelangganCap.setText("Pelanggan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblPelangganCap, gridBagConstraints);
        lblPelanggan.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblPelanggan, gridBagConstraints);
        lblMulaiCap.setForeground(new java.awt.Color(95, 107, 122));
        lblMulaiCap.setText("Mulai");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblMulaiCap, gridBagConstraints);
        lblMulai.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblMulai, gridBagConstraints);
        lblSelesaiCap.setForeground(new java.awt.Color(95, 107, 122));
        lblSelesaiCap.setText("Selesai");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblSelesaiCap, gridBagConstraints);
        lblSelesai.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblSelesai, gridBagConstraints);
        lblDurasiCap.setForeground(new java.awt.Color(95, 107, 122));
        lblDurasiCap.setText("Durasi (dibulatkan)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblDurasiCap, gridBagConstraints);
        lblDurasi.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblDurasi, gridBagConstraints);
        lblTarifCap.setForeground(new java.awt.Color(95, 107, 122));
        lblTarifCap.setText("Tarif per jam");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTarifCap, gridBagConstraints);
        lblTarif.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTarif, gridBagConstraints);
        lblTotalCap.setText("Total tagihan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTotalCap, gridBagConstraints);
        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTotal.setForeground(new java.awt.Color(31, 78, 140));
        lblTotal.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblTotal, gridBagConstraints);
        lblBayarCap.setText("Uang bayar (Rp)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblBayarCap, gridBagConstraints);
        pnlBayar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        txtBayar.setColumns(12);
        pnlBayar.add(txtBayar);
        btnPas.setText("Uang pas");
        btnPas.setFocusPainted(false);
        btnPas.setPreferredSize(new java.awt.Dimension(100, 32));
        btnPas.addActionListener(this::btnPasActionPerformed);
        pnlBayar.add(btnPas);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(pnlBayar, gridBagConstraints);
        lblKembalianCap.setText("Kembalian");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKembalianCap, gridBagConstraints);
        lblKembalian.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblKembalian.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlIsi.add(lblKembalian, gridBagConstraints);
        getContentPane().add(pnlIsi, java.awt.BorderLayout.CENTER);
        pnlTombol.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 8, 12));
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnBatal.setText("Batal");
        btnBatal.setFocusPainted(false);
        btnBatal.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        pnlTombol.add(btnBatal);
        btnBayar.setText("Bayar");
        btnBayar.setFocusPainted(false);
        btnBayar.setPreferredSize(new java.awt.Dimension(100, 32));
        btnBayar.addActionListener(this::btnBayarActionPerformed);
        pnlTombol.add(btnBayar);
        getContentPane().add(pnlTombol, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasActionPerformed
        txtBayar.setText(String.valueOf(sesi.getTotal()));
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
    private javax.swing.JLabel lblBayarCap;
    private javax.swing.JLabel lblDurasi;
    private javax.swing.JLabel lblDurasiCap;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblKembalianCap;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblKodeCap;
    private javax.swing.JLabel lblKomputer;
    private javax.swing.JLabel lblKomputerCap;
    private javax.swing.JLabel lblMulai;
    private javax.swing.JLabel lblMulaiCap;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPelangganCap;
    private javax.swing.JLabel lblSelesai;
    private javax.swing.JLabel lblSelesaiCap;
    private javax.swing.JLabel lblTarif;
    private javax.swing.JLabel lblTarifCap;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalCap;
    private javax.swing.JPanel pnlBayar;
    private javax.swing.JPanel pnlIsi;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JTextField txtBayar;
    // End of variables declaration//GEN-END:variables
}
