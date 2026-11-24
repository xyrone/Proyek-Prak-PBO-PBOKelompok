package latihan.warnetku.view;

import latihan.warnetku.dao.SesiDao;
import latihan.warnetku.dao.SesiDaoJdbc;
import latihan.warnetku.model.Sesi;

import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class RiwayatPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private static final long serialVersionUID = 1L;

    private final transient SesiDao dao = new SesiDaoJdbc();
    private transient List<Sesi> data = new ArrayList<>();
    private final DefaultTableModel model;

    public RiwayatPanel() {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        model = (DefaultTableModel) tabel.getModel();

        lblRingkasan.setFont(lblRingkasan.getFont().deriveFont(Font.BOLD, 14f));
        lblRingkasan.setForeground(Tampilan.BIRU);

        Tampilan.rapikanTabel(tabel);
        tabel.getColumnModel().getColumn(6).setCellRenderer(new Tampilan.RenderTeks(javax.swing.SwingConstants.LEFT,
                nilai -> "Berjalan".equals(nilai) ? Tampilan.ORANYE : Tampilan.HIJAU));
        int[] lebar = {200, 140, 80, 190, 130, 110, 80};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetail();
            }
        });
    }

    @Override
    public void muatUlang() {
        Tampilan.coba(this, () -> {
            data = dao.cariSemua();
            model.setRowCount(0);
            for (Sesi s : data) {
                model.addRow(new Object[]{s.getKode(), Tampilan.waktu(s.getWaktuMulai()),
                        s.getKomputer().getNama(), s.getPelanggan().toString(), s.getPetugas().getNama(),
                        Tampilan.lamaDetik(s.getDurasiDetik()), s.isBerjalan() ? "Berjalan" : "Selesai"});
            }
            lblRingkasan.setText(data.size() + " sesi");
            txtDetail.setText(data.isEmpty() ? "Belum ada sesi." : "Pilih baris untuk melihat detail sesi.");
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
        sb.append(baris("Status", s.isBerjalan() ? "Berjalan" : "Selesai"));
        sb.append(baris("Komputer", s.getKomputer().getNama() + " (" + s.getKomputer().getTipe() + ")"));
        sb.append(baris("Pelanggan", s.getPelanggan().toString()));
        sb.append(baris("Petugas", s.getPetugas().getNama()));
        sb.append(baris("Mulai", Tampilan.waktu(s.getWaktuMulai())));
        sb.append(baris("Jatah berakhir", Tampilan.waktu(s.getWaktuBerakhir())));
        sb.append(baris("Ditutup", s.isBerjalan() ? "-" : Tampilan.waktu(s.getWaktuSelesai())));
        sb.append(baris("Lama main", Tampilan.lamaDetik(s.getDurasiDetik())));
        sb.append(baris("Dikembalikan", s.isBerjalan() ? "-"
                : Tampilan.lamaDetik(s.getBillingKembaliDetik()) + " (ke billing " + s.getKomputer().getTipe() + ")"));
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

        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblJudul.setText("Riwayat Sesi");
        pnlAtas.add(lblJudul, java.awt.BorderLayout.WEST);

        lblRingkasan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        pembagi.setBorder(null);
        pembagi.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pembagi.setResizeWeight(0.4);

        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Mulai", "Komputer", "Pelanggan", "Petugas", "Lama", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);

        pembagi.setTopComponent(spTabel);

        spDetail.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail sesi"));
        spDetail.setPreferredSize(new java.awt.Dimension(100, 300));

        txtDetail.setEditable(false);
        txtDetail.setColumns(20);
        txtDetail.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
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
