package latihan.warnetku.view;

import latihan.warnetku.dao.PenggunaDao;
import latihan.warnetku.dao.PenggunaDaoJdbc;
import latihan.warnetku.exception.LoginGagalException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Pengguna;
import java.util.Optional;

public class LoginFrame extends javax.swing.JFrame {

    private final transient PenggunaDao penggunaDao = new PenggunaDaoJdbc();

    public LoginFrame() {
        initComponents();
        getRootPane().setDefaultButton(btnMasuk);
        setSize(Math.max(getWidth(), 400), getHeight());
        setLocationRelativeTo(null);
    }

    private void masuk() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        try {
            Pengguna pengguna = login(username, password);
            new DashboardFrame(pengguna).setVisible(true);
            dispose();
        } catch (WarnetException e) {
            Tampilan.error(this, e.getMessage());
            txtPassword.setText("");
            (username.isEmpty() ? txtUsername : txtPassword).requestFocusInWindow();
        }
    }

    private Pengguna login(String username, String password) throws WarnetException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new LoginGagalException("Username dan password wajib diisi");
        }
        Optional<Pengguna> pengguna = penggunaDao.cariByUsername(username);
        if (pengguna.isEmpty() || !pengguna.get().cocokPassword(password)) {
            throw new LoginGagalException("Username atau password salah");
        }
        return pengguna.get();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlHeader = new javax.swing.JPanel();
        lblNama = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        pnlForm = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnMasuk = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Warnetku - Masuk");
        setResizable(false);

        pnlHeader.setBackground(new java.awt.Color(31, 78, 140));
        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblNama.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblNama.setForeground(new java.awt.Color(255, 255, 255));
        lblNama.setText("Warnetku");
        pnlHeader.add(lblNama, java.awt.BorderLayout.CENTER);

        lblSub.setForeground(new java.awt.Color(207, 224, 247));
        pnlHeader.add(lblSub, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlForm.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 24, 24, 24));
        pnlForm.setLayout(new java.awt.GridBagLayout());

        lblUsername.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 6, 4);
        pnlForm.add(lblUsername, gridBagConstraints);

        txtUsername.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 6, 4);
        pnlForm.add(txtUsername, gridBagConstraints);

        lblPassword.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 6, 4);
        pnlForm.add(lblPassword, gridBagConstraints);

        txtPassword.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 6, 4);
        pnlForm.add(txtPassword, gridBagConstraints);

        btnMasuk.setText("Masuk");
        btnMasuk.setFocusPainted(false);
        btnMasuk.setPreferredSize(new java.awt.Dimension(100, 32));
        btnMasuk.addActionListener(this::btnMasukActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(16, 4, 4, 4);
        pnlForm.add(btnMasuk, gridBagConstraints);

        lblInfo.setForeground(new java.awt.Color(95, 107, 122));
        lblInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 4);
        pnlForm.add(lblInfo, gridBagConstraints);

        getContentPane().add(pnlForm, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
        masuk();
    }//GEN-LAST:event_btnMasukActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMasuk;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSub;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
