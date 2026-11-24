package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.StatusKomputer;
import latihan.warnetku.model.TipeKomputer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KomputerDaoJdbc implements KomputerDao {

    private static final String KOLOM = "id, nama, tipe, tarif_per_jam, status";

    @Override
    public void simpan(Komputer k) throws WarnetException {
        String sql = "INSERT INTO komputer (nama, tipe, tarif_per_jam, status) VALUES (?, ?, ?, ?)";
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.getNama());
            ps.setString(2, k.getTipe().name());
            ps.setInt(3, k.getTarifPerJam());
            ps.setString(4, k.getStatus().name());
            ps.executeUpdate();
            try (ResultSet kunci = ps.getGeneratedKeys()) {
                if (kunci.next()) {
                    k.setId(kunci.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw terjemahkan(e, k);
        }
    }

    @Override
    public void ubah(Komputer k) throws WarnetException {
        // Status DIPAKAI hanya diatur oleh sesi, jadi tidak boleh diubah manual dari sini.
        String sql = "UPDATE komputer SET nama = ?, tipe = ?, tarif_per_jam = ?, "
                + "status = IF(status = 'DIPAKAI', 'DIPAKAI', ?) WHERE id = ?";
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k.getNama());
            ps.setString(2, k.getTipe().name());
            ps.setInt(3, k.getTarifPerJam());
            ps.setString(4, k.getStatus().name());
            ps.setInt(5, k.getId());
            if (ps.executeUpdate() == 0) {
                throw new DataTidakValidException("Komputer tidak ditemukan (mungkin sudah dihapus)");
            }
        } catch (SQLException e) {
            throw terjemahkan(e, k);
        }
    }

    @Override
    public void hapus(int id) throws WarnetException {
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM komputer WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new DataTidakValidException("Komputer tidak ditemukan (mungkin sudah dihapus)");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == DaoUtil.ERR_DIPAKAI_DATA_LAIN) {
                throw new DataTidakValidException("Komputer tidak bisa dihapus karena sudah punya riwayat sesi. "
                        + "Ubah statusnya menjadi RUSAK jika tidak dipakai lagi.");
            }
            throw DaoUtil.terjemahkan(e);
        }
    }

    @Override
    public List<Komputer> cariSemua() throws WarnetException {
        return cari("SELECT " + KOLOM + " FROM komputer ORDER BY nama");
    }

    @Override
    public List<Komputer> cariTersedia() throws WarnetException {
        return cari("SELECT " + KOLOM + " FROM komputer WHERE status = 'TERSEDIA' ORDER BY nama");
    }

    @Override
    public Optional<Komputer> cariById(int id) throws WarnetException {
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + KOLOM + " FROM komputer WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(petakan(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
    }

    private List<Komputer> cari(String sql) throws WarnetException {
        List<Komputer> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
        return hasil;
    }

    private static Komputer petakan(ResultSet rs) throws SQLException {
        try {
            return new Komputer(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    TipeKomputer.valueOf(rs.getString("tipe")),
                    rs.getInt("tarif_per_jam"),
                    StatusKomputer.valueOf(rs.getString("status")));
        } catch (DataTidakValidException e) {
            throw new SQLException("Data komputer di database tidak valid: " + e.getMessage(), e);
        }
    }

    private static WarnetException terjemahkan(SQLException e, Komputer k) {
        if (e.getErrorCode() == DaoUtil.ERR_DUPLIKAT) {
            return new DataTidakValidException("Nama komputer '" + k.getNama() + "' sudah dipakai");
        }
        return DaoUtil.terjemahkan(e);
    }
}
