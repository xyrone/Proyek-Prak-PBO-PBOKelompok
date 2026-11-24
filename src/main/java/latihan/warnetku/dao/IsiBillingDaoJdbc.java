package latihan.warnetku.dao;

import latihan.warnetku.db.Koneksi;
import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.IsiBilling;
import latihan.warnetku.model.Pelanggan;
import latihan.warnetku.model.Pengguna;
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

public class IsiBillingDaoJdbc implements IsiBillingDao {

    private static final String SELECT_ISI =
            "SELECT i.id, i.id_pelanggan, i.id_petugas, i.waktu, i.tipe, i.durasi_jam, i.tarif_per_jam, "
                    + "i.total, i.bayar, i.kembalian, "
                    + "pl.kode AS pl_kode, pl.nama AS pl_nama, pl.billing_reguler AS pl_reguler, "
                    + "pl.billing_vip AS pl_vip, pl.dibuat AS pl_dibuat, "
                    + "pg.username AS pg_username, pg.password AS pg_password, pg.nama AS pg_nama "
                    + "FROM isi_billing i "
                    + "JOIN pelanggan pl ON pl.id = i.id_pelanggan "
                    + "JOIN pengguna pg ON pg.id = i.id_petugas "
                    + "ORDER BY i.waktu DESC, i.id DESC";

    @Override
    public void isi(IsiBilling isi, int uangBayar) throws WarnetException {
        isi.hitungKembalian(uangBayar); // cek cepat sebelum menyentuh database

        Connection c = null;
        try {
            c = Koneksi.getInstance().getConnection();
            c.setAutoCommit(false);

            // Kunci baris pelanggan supaya tambah dan kurang billing tidak saling menimpa.
            int billingLama;
            String kolom = isi.getTipe() == TipeKomputer.VIP ? "billing_vip" : "billing_reguler";
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT " + kolom + " FROM pelanggan WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, isi.getPelanggan().getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataTidakValidException("Pelanggan tidak ditemukan (mungkin sudah dihapus)");
                    }
                    billingLama = rs.getInt(1);
                }
            }

            // Tarif diambil berdasarkan jenis billing, bukan nomor PC yang dipilih pengguna.
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT tarif_per_jam FROM komputer WHERE tipe = ? ORDER BY id LIMIT 1")) {
                ps.setString(1, isi.getTipe().name());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataTidakValidException("Tarif untuk billing " + isi.getTipe()
                                + " belum tersedia. Tambahkan komputer dengan jenis tersebut terlebih dahulu.");
                    }
                    isi.setTipeDanTarif(isi.getTipe(), rs.getInt(1));
                }
            }
            isi.bayar(uangBayar);

            long baru = (long) billingLama + isi.getDetik();
            if (baru > Pelanggan.BILLING_MAKS_DETIK) {
                throw new DataTidakValidException("Billing " + isi.getTipe() + " maksimal "
                        + (Pelanggan.BILLING_MAKS_DETIK / 3600) + " jam per pelanggan");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pelanggan SET " + kolom + " = ? WHERE id = ?")) {
                ps.setInt(1, (int) baru);
                ps.setInt(2, isi.getPelanggan().getId());
                ps.executeUpdate();
            }

            isi.setWaktu(LocalDateTime.now());
            String sql = "INSERT INTO isi_billing (id_pelanggan, id_petugas, waktu, tipe, durasi_jam, "
                    + "tarif_per_jam, total, bayar, kembalian) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, isi.getPelanggan().getId());
                ps.setInt(2, isi.getPetugas().getId());
                ps.setTimestamp(3, Timestamp.valueOf(isi.getWaktu()));
                ps.setString(4, isi.getTipe().name());
                ps.setInt(5, isi.getDurasiJam());
                ps.setInt(6, isi.getTarifPerJam());
                ps.setInt(7, isi.getTotal());
                ps.setInt(8, isi.getBayar());
                ps.setInt(9, isi.getKembalian());
                ps.executeUpdate();
                try (ResultSet kunci = ps.getGeneratedKeys()) {
                    if (kunci.next()) {
                        isi.setId(kunci.getInt(1));
                    }
                }
            }
            c.commit();
            isi.getPelanggan().setBillingDetik(isi.getTipe(), (int) baru);
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
    public List<IsiBilling> cariSemua() throws WarnetException {
        List<IsiBilling> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ISI);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pelanggan pelanggan = PelangganDaoJdbc.petakan(rs, "id_pelanggan", "pl_kode", "pl_nama", "pl_reguler", "pl_vip", "pl_dibuat");
                Pengguna petugas = PenggunaDaoJdbc.petakan(rs.getInt("id_petugas"), rs.getString("pg_username"),
                        rs.getString("pg_password"), rs.getString("pg_nama"));
                hasil.add(new IsiBilling(rs.getInt("id"), pelanggan, petugas,
                        rs.getTimestamp("waktu").toLocalDateTime(), TipeKomputer.valueOf(rs.getString("tipe")),
                        rs.getInt("durasi_jam"), rs.getInt("tarif_per_jam"), rs.getInt("total"),
                        rs.getInt("bayar"), rs.getInt("kembalian")));
            }
        } catch (SQLException e) {
            throw DaoUtil.terjemahkan(e);
        }
        return hasil;
    }
}
