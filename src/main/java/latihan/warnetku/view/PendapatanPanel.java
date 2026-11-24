package latihan.warnetku.view;

import latihan.warnetku.dao.IsiBillingDao;
import latihan.warnetku.dao.IsiBillingDaoJdbc;
import latihan.warnetku.model.IsiBilling;

import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.util.List;

public class PendapatanPanel extends javax.swing.JPanel implements DapatDimuatUlang {

    private static final long serialVersionUID = 1L;

    private final transient IsiBillingDao dao = new IsiBillingDaoJdbc();
    private final DefaultTableModel model;

    public PendapatanPanel() {
        initComponents();
        ((java.awt.BorderLayout) getLayout()).setVgap(12);
        model = (DefaultTableModel) tabel.getModel();

        lblRingkasan.setFont(lblRingkasan.getFont().deriveFont(Font.BOLD, 14f));
        lblRingkasan.setForeground(Tampilan.BIRU);

        Tampilan.rapikanTabel(tabel, 5, 6, 7);
        int[] lebar = {140, 200, 130, 80, 80, 100, 100, 100};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    @Override
    public void muatUlang() {
        Tampilan.coba(this, () -> {
            List<IsiBilling> data = dao.cariSemua();
            model.setRowCount(0);
            long pendapatan = 0;
            for (IsiBilling i : data) {
                model.addRow(new Object[]{Tampilan.waktu(i.getWaktu()), i.getPelanggan().toString(),
                        i.getPetugas().getNama(), i.getTipe(), Tampilan.lama(i.getDurasiJam()),
                        Tampilan.rupiah(i.getTarifPerJam()), Tampilan.rupiah(i.getTotal()),
                        Tampilan.rupiah(i.getKembalian())});
                pendapatan += i.getTotal();
            }
            lblRingkasan.setText(data.size() + " transaksi  |  Total pendapatan: "
                    + Tampilan.rupiah((int) Math.min(pendapatan, Integer.MAX_VALUE)));
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblRingkasan = new javax.swing.JLabel();
        btnSegarkan = new javax.swing.JButton();
        spTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setLayout(new java.awt.BorderLayout());

        pnlAtas.setLayout(new java.awt.BorderLayout());

        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblJudul.setText("Pendapatan (Isi Billing)");
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

        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Waktu", "Pelanggan", "Petugas", "Billing", "Tambah", "Tarif / jam", "Total", "Kembalian"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTabel.setViewportView(tabel);

        add(spTabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muatUlang();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblRingkasan;
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JScrollPane spTabel;
    private javax.swing.JTable tabel;
    // End of variables declaration//GEN-END:variables
}
