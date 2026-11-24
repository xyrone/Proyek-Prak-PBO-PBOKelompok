package latihan.warnetku.view;

import latihan.warnetku.model.Admin;
import latihan.warnetku.model.Pengguna;
import java.awt.Component;
import java.awt.Dimension;

public class DashboardFrame extends javax.swing.JFrame {

    public DashboardFrame(Pengguna pengguna) {
        initComponents();
        setTitle("Warnetku - " + pengguna.getNama() + " (" + pengguna.getRole() + ")");
        lblAkun.setText(pengguna.getNama() + "  |  " + pengguna.getRole() + "   ");
        if (pengguna instanceof Admin) {
            tab.addTab("Komputer", new KomputerPanel());
        } else {
            tab.addTab("Sesi", new SesiPanel(pengguna));
        }
        tab.addTab("Riwayat", new RiwayatPanel());
        tab.addChangeListener(e -> muatUlangTabAktif());
        setMinimumSize(new Dimension(900, 560));
        setSize(1020, 660);
        setLocationRelativeTo(null);
        muatUlangTabAktif();
    }

    private void muatUlangTabAktif() {
        Component aktif = tab.getSelectedComponent();
        if (aktif instanceof DapatDimuatUlang) {
            ((DapatDimuatUlang) aktif).muatUlang();
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        pnlKanan = new javax.swing.JPanel();
        lblAkun = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tab = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlBar.setBackground(new java.awt.Color(31, 78, 140));
        pnlBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));
        pnlBar.setLayout(new java.awt.BorderLayout());
        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setText("Warnetku");
        pnlBar.add(lblLogo, java.awt.BorderLayout.WEST);
        pnlKanan.setOpaque(false);
        lblAkun.setForeground(new java.awt.Color(207, 224, 247));
        lblAkun.setText(" ");
        pnlKanan.add(lblAkun);
        btnLogout.setText("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new java.awt.Dimension(100, 32));
        btnLogout.addActionListener(this::btnLogoutActionPerformed);
        pnlKanan.add(btnLogout);
        pnlBar.add(pnlKanan, java.awt.BorderLayout.EAST);
        getContentPane().add(pnlBar, java.awt.BorderLayout.NORTH);
        getContentPane().add(tab, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        logout();
    }//GEN-LAST:event_btnLogoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    private javax.swing.JLabel lblAkun;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JPanel pnlBar;
    private javax.swing.JPanel pnlKanan;
    private javax.swing.JTabbedPane tab;
    // End of variables declaration//GEN-END:variables
}
