package latihan.warnetku.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Koneksi {
    private static final String URL_BAWAAN =
            "jdbc:mysql://localhost:3306/warnetku";
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

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String ambil(String nama, String bawaan) {
        String nilai = System.getenv(nama);
        return (nilai == null) ? bawaan : nilai;
    }
}
