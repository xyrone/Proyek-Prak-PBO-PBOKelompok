package latihan.warnetku.view;

import latihan.warnetku.dao.SesiDao;
import latihan.warnetku.dao.SesiDaoJdbc;
import latihan.warnetku.model.Sesi;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class RiwayatPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private final transient SesiDao dao = new SesiDaoJdbc();
    private transient List<Sesi> data = new ArrayList<>();
    private final DefaultTableModel model;

    public RiwayatPanel() {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        model = (DefaultTableModel) tabel.getModel();
        Tampilan.rapikanTabel(tabel, 5, 6);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetail();
            }
        });
    }

    @Override
    public void muatUlang() {
        Tampilan.coba(this, () -> {
            data = dao.cariSelesai();
            model.setRowCount(0);
            long pendapatan = 0;
            for (Sesi s : data) {
                model.addRow(new Object[]{s.getKode(), Tampilan.waktu(s.getWaktuSelesai()),
                        s.getKomputer().getNama(), s.getNamaPelanggan(), s.getOperator().getNama(),
                        Tampilan.durasi(s.getDurasiMenit()), Tampilan.rupiah(s.getTotal())});
                pendapatan += s.getTotal();
            }
            lblRingkasan.setText(data.size() + " sesi selesai  |  Total pendapatan: "
                    + Tampilan.rupiah((int) Math.min(pendapatan, Integer.MAX_VALUE)));
            txtDetail.setText(data.isEmpty() ? "Belum ada sesi yang selesai." : "Pilih baris untuk melihat detail sesi.");
        });
    }

    private void tampilkanDetail() {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= data.size()) {
            return;
        }
        Sesi s = data.get(baris);
        StringBuilder sb = new StringBuilder();
        sb.append(baris("Kode sesi", s.getKode()));
        sb.append(baris("Komputer", s.getKomputer().getNama() + " (" + s.getKomputer().getTipe() + ")"));
        sb.append(baris("Pelanggan", s.getNamaPelanggan()));
        sb.append(baris("Operator", s.getOperator().getNama()));
        sb.append(baris("Mulai", Tampilan.waktu(s.getWaktuMulai())));
        sb.append(baris("Selesai", Tampilan.waktu(s.getWaktuSelesai())));
        sb.append(baris("Durasi tertagih", Tampilan.durasi(s.getDurasiMenit())));
        sb.append(baris("Tarif per jam", Tampilan.rupiah(s.getTarifPerJam())));
        sb.append(baris("Total", Tampilan.rupiah(s.getTotal())));
        sb.append(baris("Uang bayar", Tampilan.rupiah(s.getBayar())));
        sb.append(baris("Kembalian", Tampilan.rupiah(s.getKembalian())));
        txtDetail.setText(sb.toString());
        txtDetail.setCaretPosition(0);
    }

    private static String baris(String label, String nilai) {
        return String.format("%-16s: %s%n", label, nilai);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblRingkasan = new javax.swing.JLabel();
        btnSegarkan = new javax.swing.JButton();
        pembagi = new javax.swing.JSplitPane();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        spDetail = new javax.swing.JScrollPane();
        txtDetail = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlAtas.setLayout(new java.awt.BorderLayout());
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblJudul.setText("Riwayat Sesi");
        pnlAtas.add(lblJudul, java.awt.BorderLayout.WEST);
        lblRingkasan.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblRingkasan.setForeground(new java.awt.Color(31, 78, 140));
        lblRingkasan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRingkasan.setText(" ");
        pnlAtas.add(lblRingkasan, java.awt.BorderLayout.CENTER);
        btnSegarkan.setText("Segarkan");
        btnSegarkan.setFocusPainted(false);
        btnSegarkan.setPreferredSize(new java.awt.Dimension(100, 32));
        btnSegarkan.addActionListener(this::btnSegarkanActionPerformed);
        pnlAtas.add(btnSegarkan, java.awt.BorderLayout.EAST);
        add(pnlAtas, java.awt.BorderLayout.NORTH);
        pembagi.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pembagi.setResizeWeight(0.5);
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Tanggal", "Komputer", "Pelanggan", "Operator", "Durasi", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);
        pembagi.setTopComponent(spTabel);
        spDetail.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail sesi"));
        spDetail.setPreferredSize(new java.awt.Dimension(100, 250));
        txtDetail.setEditable(false);
        txtDetail.setFont(new java.awt.Font("Monospaced", 0, 13));
        txtDetail.setMargin(new java.awt.Insets(8, 10, 8, 10));
        spDetail.setViewportView(txtDetail);
        pembagi.setBottomComponent(spDetail);
        add(pembagi, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muatUlang();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblRingkasan;
    private javax.swing.JSplitPane pembagi;
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JScrollPane spDetail;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JTextArea txtDetail;
    // End of variables declaration//GEN-END:variables
}
