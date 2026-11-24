package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Pelanggan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PelangganDaoJdbc implements PelangganDao {

    private static final String KOLOM = "id, kode, nama, billing_reguler, billing_vip, dibuat";

    /** Membuat pelanggan baru; ID (kode) dibuat dari nomor urut baris, contoh PLG-0001. */
    @Override
    public void simpan(Pelanggan p) throws WarnetException {
        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);
            int id;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO pelanggan (kode, nama, billing_reguler, billing_vip, dibuat) VALUES (?, ?, 0, 0, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "TMP-" + System.nanoTime()); // diganti setelah id diketahui
                ps.setString(2, p.getNama());
                ps.setTimestamp(3, Timestamp.valueOf(p.getDibuat()));
                ps.executeUpdate();
                try (ResultSet kunci = ps.getGeneratedKeys()) {
                    kunci.next();
                    id = kunci.getInt(1);
                }
            }
            String kode = Pelanggan.buatKode(id);
            try (PreparedStatement ps = c.prepareStatement("UPDATE pelanggan SET kode = ? WHERE id = ?")) {
                ps.setString(1, kode);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            c.commit();
            p.setId(id);
            p.setKode(kode);
        } catch (SQLException e) {
            DaoUtil.rollbackDiam(c);
            throw DaoUtil.terjemahkan(e);
        } finally {
            DaoUtil.tutupDiam(c);
        }
    }

    /** Hanya nama yang boleh diubah; billing diatur lewat isi billing dan sesi. */
    @Override
    public void ubah(Pelanggan p) throws WarnetException {
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE pelanggan SET nama = ? WHERE id = ?")) {
            ps.setString(1, p.getNama());
            ps.setInt(2, p.getId());
            if (ps.executeUpdate() == 0) {
                throw new DataTidakValidException("Pelanggan tidak ditemukan (mungkin sudah dihapus)");
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
    }

    @Override
    public void hapus(int id) throws WarnetException {
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM pelanggan WHERE id = ? AND billing_reguler = 0 AND billing_vip = 0")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                if (cariById(id).isPresent()) {
                    throw new DataTidakValidException("Pelanggan masih punya sisa billing, tidak bisa dihapus");
                }
                throw new DataTidakValidException("Pelanggan tidak ditemukan (mungkin sudah dihapus)");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == DaoUtil.ERR_DIPAKAI_DATA_LAIN) {
                throw new DataTidakValidException("Pelanggan tidak bisa dihapus karena sudah punya riwayat sesi atau isi billing");
            }
            throw DaoUtil.terjemahkan(e);
        }
    }

    @Override
    public List<Pelanggan> cariSemua() throws WarnetException {
        List<Pelanggan> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + KOLOM + " FROM pelanggan ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
        return hasil;
    }

    @Override
    public Optional<Pelanggan> cariById(int id) throws WarnetException {
        return cariSatu("SELECT " + KOLOM + " FROM pelanggan WHERE id = ?", String.valueOf(id), true);
    }

    @Override
    public Optional<Pelanggan> cariByKode(String kode) throws WarnetException {
        return cariSatu("SELECT " + KOLOM + " FROM pelanggan WHERE kode = ?", kode, false);
    }

    private Optional<Pelanggan> cariSatu(String sql, String nilai, boolean angka) throws WarnetException {
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (angka) {
                ps.setInt(1, Integer.parseInt(nilai));
            } else {
                ps.setString(1, nilai);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(petakan(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
    }

    private static Pelanggan petakan(ResultSet rs) throws SQLException {
        return petakan(rs, "id", "kode", "nama", "billing_reguler", "billing_vip", "dibuat");
    }

    /** Dipakai juga oleh DAO lain yang menggabungkan tabel pelanggan dengan alias kolom berbeda. */
    static Pelanggan petakan(ResultSet rs, String id, String kode, String nama, String reguler, String vip,
                             String dibuat) throws SQLException {
        try {
            return new Pelanggan(rs.getInt(id), rs.getString(kode), rs.getString(nama),
                    rs.getInt(reguler), rs.getInt(vip), rs.getTimestamp(dibuat).toLocalDateTime());
        } catch (DataTidakValidException e) {
            throw new SQLException("Data pelanggan di database tidak valid: " + e.getMessage(), e);
        }
    }
}
