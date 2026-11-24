package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.KomputerTidakTersediaException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;
import latihan.warnetku.model.Pelanggan;
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
            "SELECT s.id, s.kode, s.id_komputer, s.id_petugas, s.id_pelanggan, s.waktu_mulai, "
                    + "s.waktu_selesai, s.durasi_menit, s.billing_kembali_detik, "
                    + "k.nama AS k_nama, k.tipe AS k_tipe, k.tarif_per_jam AS k_tarif, k.status AS k_status, "
                    + "p.username AS p_username, p.password AS p_password, p.nama AS p_nama, "
                    + "pl.kode AS pl_kode, pl.nama AS pl_nama, pl.billing_reguler AS pl_reguler, "
                    + "pl.billing_vip AS pl_vip, pl.dibuat AS pl_dibuat "
                    + "FROM sesi s "
                    + "JOIN komputer k ON k.id = s.id_komputer "
                    + "JOIN pengguna p ON p.id = s.id_petugas "
                    + "JOIN pelanggan pl ON pl.id = s.id_pelanggan ";

    private static String kolomBilling(Komputer k) {
        return k.getTipe() == TipeKomputer.VIP ? "billing_vip" : "billing_reguler";
    }

    @Override
    public void mulai(Sesi sesi) throws WarnetException {
        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);
            String kolom = kolomBilling(sesi.getKomputer());
            int idPelanggan = sesi.getPelanggan().getId();

            // Kunci baris pelanggan: billing dibaca dari database (bukan dari data tampilan yang mungkin lama).
            int billing;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT " + kolom + " FROM pelanggan WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, idPelanggan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataTidakValidException("Pelanggan tidak ditemukan (mungkin sudah dihapus)");
                    }
                    billing = rs.getInt(1);
                }
            }

            // Satu pelanggan hanya boleh punya satu sesi berjalan.
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM sesi WHERE id_pelanggan = ? AND waktu_selesai IS NULL")) {
                ps.setInt(1, idPelanggan);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new DataTidakValidException(sesi.getPelanggan().getNama()
                                + " masih punya sesi yang berjalan. Selesaikan dulu sesi tersebut.");
                    }
                }
            }

            sesi.cekBilling(billing);

            // Ambil komputer hanya jika masih TERSEDIA; kalau tidak ada baris yang berubah, ditolak.
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE komputer SET status = 'DIPAKAI' WHERE id = ? AND status = 'TERSEDIA'")) {
                ps.setInt(1, sesi.getKomputer().getId());
                if (ps.executeUpdate() == 0) {
                    throw new KomputerTidakTersediaException("Komputer " + sesi.getKomputer().getNama()
                            + " sedang dipakai atau rusak. Pilih komputer lain.");
                }
            }

            int sisa = billing - sesi.getDurasiDetik();
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pelanggan SET " + kolom + " = ? WHERE id = ?")) {
                ps.setInt(1, sisa);
                ps.setInt(2, idPelanggan);
                ps.executeUpdate();
            }

            sesi.mulaiPada(LocalDateTime.now());
            String sql = "INSERT INTO sesi (kode, id_komputer, id_petugas, id_pelanggan, waktu_mulai, "
                    + "durasi_menit) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sesi.getKode());
                ps.setInt(2, sesi.getKomputer().getId());
                ps.setInt(3, sesi.getPetugas().getId());
                ps.setInt(4, idPelanggan);
                ps.setTimestamp(5, Timestamp.valueOf(sesi.getWaktuMulai()));
                ps.setInt(6, sesi.getDurasiMenit());
                ps.executeUpdate();
                try (ResultSet kunci = ps.getGeneratedKeys()) {
                    if (kunci.next()) {
                        sesi.setId(kunci.getInt(1));
                    }
                }
            }
            c.commit();
            sesi.getPelanggan().setBillingDetik(sesi.getKomputer().getTipe(), sisa);
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
    public void selesaikan(Sesi sesi) throws WarnetException {
        sesi.selesai(LocalDateTime.now());

        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE sesi SET waktu_selesai = ?, billing_kembali_detik = ? "
                            + "WHERE id = ? AND waktu_selesai IS NULL")) {
                ps.setTimestamp(1, Timestamp.valueOf(sesi.getWaktuSelesai()));
                ps.setInt(2, sesi.getBillingKembaliDetik());
                ps.setInt(3, sesi.getId());
                if (ps.executeUpdate() == 0) {
                    throw new DataTidakValidException("Sesi " + sesi.getKode() + " sudah diakhiri sebelumnya");
                }
            }

            // Sisa waktu dikembalikan ke billing pelanggan.
            String kolom = kolomBilling(sesi.getKomputer());
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pelanggan SET " + kolom + " = " + kolom + " + ? WHERE id = ?")) {
                ps.setInt(1, sesi.getBillingKembaliDetik());
                ps.setInt(2, sesi.getPelanggan().getId());
                ps.executeUpdate();
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
    public List<Sesi> cariSemua() throws WarnetException {
        return cari(SELECT_SESI + "ORDER BY s.waktu_mulai DESC, s.id DESC");
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
            Pengguna petugas = PenggunaDaoJdbc.petakan(
                    rs.getInt("id_petugas"),
                    rs.getString("p_username"),
                    rs.getString("p_password"),
                    rs.getString("p_nama"));
            Pelanggan pelanggan = PelangganDaoJdbc.petakan(rs, "id_pelanggan", "pl_kode", "pl_nama",
                    "pl_reguler", "pl_vip", "pl_dibuat");
            Timestamp mulai = rs.getTimestamp("waktu_mulai");
            Timestamp selesai = rs.getTimestamp("waktu_selesai");
            return new Sesi(
                    rs.getInt("id"),
                    rs.getString("kode"),
                    komputer,
                    petugas,
                    pelanggan,
                    mulai.toLocalDateTime(),
                    selesai == null ? null : selesai.toLocalDateTime(),
                    rs.getInt("durasi_menit"),
                    rs.getInt("billing_kembali_detik"));
        } catch (DataTidakValidException e) {
            throw new SQLException("Data sesi di database tidak valid: " + e.getMessage(), e);
        }
    }
}
