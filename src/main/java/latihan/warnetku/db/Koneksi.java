package latihan.warnetku.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Pengatur koneksi ke MySQL (singleton).
 *
 * Nilai bawaan cocok untuk Laragon (root, tanpa password). Untuk mengubahnya,
 * edit konstanta di bawah, atau set environment variable
 * WARNETKU_DB_URL, WARNETKU_DB_USER, WARNETKU_DB_PASSWORD.
 */
public final class Koneksi {

    private static final String URL_BAWAAN =
            "jdbc:mysql://localhost:3306/warnetku?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER_BAWAAN = "root";
    private static final String PASSWORD_BAWAAN = "";

    private static Koneksi instance;

    private final String url;
    private final String user;
    private final String password;

    private Koneksi() {
        this.url = ambil("WARNETKU_DB_URL", URL_BAWAAN);
        this.user = ambil("WARNETKU_DB_USER", USER_BAWAAN);
        this.password = ambil("WARNETKU_DB_PASSWORD", PASSWORD_BAWAAN);
    }

    public static synchronized Koneksi getInstance() {
        if (instance == null) {
            instance = new Koneksi();
        }
        return instance;
    }

    /** Membuka koneksi baru. Pemanggil wajib menutupnya (try-with-resources / finally). */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String ambil(String nama, String bawaan) {
        String nilai = System.getenv(nama);
        return (nilai == null) ? bawaan : nilai;
    }
}
