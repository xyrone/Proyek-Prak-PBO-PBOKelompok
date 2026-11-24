package latihan.warnetku.dao;

import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;

import java.sql.SQLException;

/** Penerjemah SQLException menjadi pesan yang dimengerti pengguna. */
final class DaoUtil {

    static final int ERR_DUPLIKAT = 1062;
    static final int ERR_DIPAKAI_DATA_LAIN = 1451;

    private DaoUtil() {
    }

    static WarnetException terjemahkan(SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) {
            return new WarnetException("Tidak dapat terhubung ke database MySQL. "
                    + "Pastikan MySQL (Laragon) sudah berjalan.", e);
        }
        if (e.getErrorCode() == ERR_DUPLIKAT) {
            return new DataTidakValidException("Data sudah ada (duplikat)");
        }
        return new WarnetException("Kesalahan database: " + e.getMessage(), e);
    }

    /** Membatalkan transaksi tanpa menutupi exception aslinya. */
    static void rollbackDiam(java.sql.Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (SQLException ignored) {
                // exception aslinya lebih penting
            }
        }
    }

    static void tutupDiam(java.sql.Connection c) {
        if (c != null) {
            try {
                c.setAutoCommit(true);
                c.close();
            } catch (SQLException ignored) {
                // tidak ada yang bisa dilakukan
            }
        }
    }
}
