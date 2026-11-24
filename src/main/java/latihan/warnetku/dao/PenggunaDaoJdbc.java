package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Pengguna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PenggunaDaoJdbc implements PenggunaDao {

    @Override
    public Optional<Pengguna> cariByUsername(String username) throws WarnetException {
        String sql = "SELECT id, username, password, nama FROM pengguna WHERE username = ?";
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(petakan(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("nama")));
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
    }

    /** Membuat objek Pengguna dari kolom tabel. Dipakai juga oleh SesiDaoJdbc. */
    static Pengguna petakan(int id, String username, String hash, String nama) throws SQLException {
        try {
            return new Pengguna(id, username, hash, nama);
        } catch (DataTidakValidException e) {
            throw new SQLException("Data pengguna di database tidak valid: " + e.getMessage(), e);
        }
    }
}
