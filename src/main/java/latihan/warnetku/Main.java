package latihan.warnetku;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.view.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;
import java.sql.SQLException;

/** Titik masuk aplikasi Warnetku. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            pasangTampilan();
            if (!databaseSiap()) {
                System.exit(1);
            }
            new LoginFrame().setVisible(true);
        });
    }

    /** Menguji koneksi sekali di awal supaya kesalahan setelan terlihat jelas, bukan saat login. */
    private static boolean databaseSiap() {
        try (Connection c = Koneksi.getInstance().getConnection()) {
            return c.isValid(3);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat terhubung ke database MySQL.\n\n"
                            + "1. Pastikan MySQL (Laragon) sudah dijalankan.\n"
                            + "2. Impor database/schema.sql terlebih dahulu.\n"
                            + "3. Cek URL, user, dan password di db/Koneksi.java.\n\n"
                            + "Detail: " + e.getMessage(),
                    "Warnetku - Koneksi gagal", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Memakai tema Nimbus bila ada; kalau tidak, tampilan bawaan Swing. */
    private static void pasangTampilan() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            // tetap jalan dengan tampilan bawaan
        }
    }
}
