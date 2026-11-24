package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.KomputerTidakTersediaException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pengguna;
import latihan.warnetku.model.Sesi;
import latihan.warnetku.model.StatusKomputer;
import latihan.warnetku.model.TipeKomputer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SesiDaoJdbc implements SesiDao {
    private static final String SELECT_SESI =
            "SELECT s.id, s.kode, s.id_komputer, s.id_operator, s.nama_pelanggan, s.waktu_mulai, "
                    + "s.waktu_selesai, s.durasi_menit, s.tarif_per_jam, s.total, s.bayar, s.kembalian, "
                    + "k.nama AS k_nama, k.tipe AS k_tipe, k.tarif_per_jam AS k_tarif, k.status AS k_status, "
                    + "p.username AS p_username, p.password AS p_password, p.nama AS p_nama, p.role AS p_role "
                    + "FROM sesi s "
                    + "JOIN komputer k ON k.id = s.id_komputer "
                    + "JOIN pengguna p ON p.id = s.id_operator ";

    @Override
    public void mulai(Sesi sesi) throws WarnetException {
        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE komputer SET status = 'DIPAKAI' WHERE id = ? AND status = 'TERSEDIA'")) {
                ps.setInt(1, sesi.getKomputer().getId());
                if (ps.executeUpdate() == 0) {
                    throw new KomputerTidakTersediaException("Komputer " + sesi.getKomputer().getNama()
                            + " sedang dipakai atau rusak. Pilih komputer lain.");
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT tarif_per_jam FROM komputer WHERE id = ?")) {
                ps.setInt(1, sesi.getKomputer().getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sesi.setTarifPerJam(rs.getInt(1));
                    }
                }
            }

            String sql = "INSERT INTO sesi (kode, id_komputer, id_operator, nama_pelanggan, "
                    + "waktu_mulai, tarif_per_jam) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sesi.getKode());
                ps.setInt(2, sesi.getKomputer().getId());
                ps.setInt(3, sesi.getOperator().getId());
                ps.setString(4, sesi.getNamaPelanggan());
                ps.setTimestamp(5, Timestamp.valueOf(sesi.getWaktuMulai()));
                ps.setInt(6, sesi.getTarifPerJam());
                ps.executeUpdate();
                try (ResultSet kunci = ps.getGeneratedKeys()) {
                    if (kunci.next()) {
                        sesi.setId(kunci.getInt(1));
                    }
                }
            }
            c.commit();
        } catch (SQLException e) {
            DaoUtil.rollbackDiam(c);
            throw DaoUtil.terjemahkan(e);
        } catch (WarnetException e) {
            DaoUtil.rollbackDiam(c);
            throw e;
        } finally {
            DaoUtil.tutupDiam(c);
        }
    }

    @Override
    public void selesaikan(Sesi sesi, int uangBayar) throws WarnetException {
        sesi.bayar(uangBayar);

        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);

            String sql = "UPDATE sesi SET waktu_selesai = ?, durasi_menit = ?, total = ?, bayar = ?, "
                    + "kembalian = ? WHERE id = ? AND waktu_selesai IS NULL";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(sesi.getWaktuSelesai()));
                ps.setInt(2, sesi.getDurasiMenit());
                ps.setInt(3, sesi.getTotal());
                ps.setInt(4, sesi.getBayar());
                ps.setInt(5, sesi.getKembalian());
                ps.setInt(6, sesi.getId());
                if (ps.executeUpdate() == 0) {
                    throw new DataTidakValidException("Sesi " + sesi.getKode() + " sudah selesai sebelumnya");
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE komputer SET status = 'TERSEDIA' WHERE id = ? AND status = 'DIPAKAI'")) {
                ps.setInt(1, sesi.getKomputer().getId());
                if (ps.executeUpdate() == 0) {
                    throw new DataTidakValidException("Status komputer " + sesi.getKomputer().getNama()
                            + " tidak sesuai (bukan DIPAKAI). Transaksi dibatalkan.");
                }
            }
            c.commit();
        } catch (SQLException e) {
            DaoUtil.rollbackDiam(c);
            throw DaoUtil.terjemahkan(e);
        } catch (WarnetException e) {
            DaoUtil.rollbackDiam(c);
            throw e;
        } finally {
            DaoUtil.tutupDiam(c);
        }
    }

    @Override
    public List<Sesi> cariBerjalan() throws WarnetException {
        return cari(SELECT_SESI + "WHERE s.waktu_selesai IS NULL ORDER BY s.waktu_mulai, s.id");
    }

    @Override
    public List<Sesi> cariSelesai() throws WarnetException {
        return cari(SELECT_SESI + "WHERE s.waktu_selesai IS NOT NULL ORDER BY s.waktu_selesai DESC, s.id DESC");
    }

    private List<Sesi> cari(String sql) throws WarnetException {
        List<Sesi> hasil = new ArrayList<>();
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

    private static Sesi petakan(ResultSet rs) throws SQLException {
        try {
            Komputer komputer = new Komputer(
                    rs.getInt("id_komputer"),
                    rs.getString("k_nama"),
                    TipeKomputer.valueOf(rs.getString("k_tipe")),
                    rs.getInt("k_tarif"),
                    StatusKomputer.valueOf(rs.getString("k_status")));
            Pengguna operator = PenggunaDaoJdbc.petakan(
                    rs.getInt("id_operator"),
                    rs.getString("p_username"),
                    rs.getString("p_password"),
                    rs.getString("p_nama"),
                    rs.getString("p_role"));
            Timestamp mulai = rs.getTimestamp("waktu_mulai");
            Timestamp selesai = rs.getTimestamp("waktu_selesai");
            return new Sesi(
                    rs.getInt("id"),
                    rs.getString("kode"),
                    komputer,
                    operator,
                    rs.getString("nama_pelanggan"),
                    mulai.toLocalDateTime(),
                    selesai == null ? null : selesai.toLocalDateTime(),
                    rs.getInt("tarif_per_jam"),
                    rs.getInt("durasi_menit"),
                    rs.getInt("total"),
                    rs.getInt("bayar"),
                    rs.getInt("kembalian"));
        } catch (DataTidakValidException e) {
            throw new SQLException("Data sesi di database tidak valid: " + e.getMessage(), e);
        }
    }
}
