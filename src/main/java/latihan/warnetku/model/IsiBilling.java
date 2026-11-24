package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.UangBayarKurangException;

import java.time.LocalDateTime;

/**
 * Pembelian billing (waktu main) oleh pelanggan. Dibayar di awal: total = jam x tarif per jam
 * komputer acuan. Tarif dan tipe disalin sehingga perubahan tarif nanti tidak mengubah riwayat.
 */
public class IsiBilling {

    public static final int JAM_MAKS = 24;

    private int id;
    private final Pelanggan pelanggan;
    private final Pengguna petugas;
    private LocalDateTime waktu;
    private TipeKomputer tipe;
    private int durasiJam;
    private int tarifPerJam;
    private int total;
    private int bayar;
    private int kembalian;
    private final Komputer acuan; // null jika dibaca dari database

    public IsiBilling(Pelanggan pelanggan, Pengguna petugas, TipeKomputer tipe, int tarifPerJam, int durasiJam)
            throws DataTidakValidException {
        if (pelanggan == null) {
            throw new DataTidakValidException("Pelanggan wajib dipilih");
        }
        if (petugas == null) {
            throw new DataTidakValidException("Petugas tidak diketahui");
        }
        if (tipe == null) {
            throw new DataTidakValidException("Jenis billing wajib dipilih");
        }
        this.pelanggan = pelanggan;
        this.petugas = petugas;
        this.acuan = null;
        setDurasiJam(durasiJam);
        this.tipe = tipe;
        if (tarifPerJam <= 0) {
            throw new DataTidakValidException("Tarif per jam harus lebih dari 0");
        }
        this.tarifPerJam = tarifPerJam;
        this.total = hitungTotal();
        this.waktu = LocalDateTime.now().withNano(0);
    }

    public IsiBilling(Pelanggan pelanggan, Pengguna petugas, Komputer acuan, int durasiJam)
            throws DataTidakValidException {
        if (pelanggan == null) {
            throw new DataTidakValidException("Pelanggan wajib dipilih");
        }
        if (petugas == null) {
            throw new DataTidakValidException("Petugas tidak diketahui");
        }
        if (acuan == null) {
            throw new DataTidakValidException("Jenis komputer wajib dipilih");
        }
        this.pelanggan = pelanggan;
        this.petugas = petugas;
        this.acuan = acuan;
        setDurasiJam(durasiJam);
        this.tipe = acuan.getTipe();
        this.tarifPerJam = acuan.getTarifPerJam();
        this.total = hitungTotal();
        this.waktu = LocalDateTime.now().withNano(0);
    }

    public IsiBilling(int id, Pelanggan pelanggan, Pengguna petugas, LocalDateTime waktu, TipeKomputer tipe,
                      int durasiJam, int tarifPerJam, int total, int bayar, int kembalian) {
        this.id = id;
        this.pelanggan = pelanggan;
        this.petugas = petugas;
        this.acuan = null;
        this.waktu = waktu;
        this.tipe = tipe;
        this.durasiJam = durasiJam;
        this.tarifPerJam = tarifPerJam;
        this.total = total;
        this.bayar = bayar;
        this.kembalian = kembalian;
    }

    public final int hitungTotal() throws DataTidakValidException {
        long hasil = (long) durasiJam * tarifPerJam;
        if (hasil > Integer.MAX_VALUE) {
            throw new DataTidakValidException("Total biaya terlalu besar");
        }
        return (int) hasil;
    }

    public final int hitungKembalian(int uangBayar) throws UangBayarKurangException {
        if (uangBayar < total) {
            throw new UangBayarKurangException("Uang bayar kurang Rp "
                    + String.format("%,d", total - uangBayar).replace(',', '.') + " dari total Rp "
                    + String.format("%,d", total).replace(',', '.'));
        }
        return uangBayar - total;
    }

    public final void bayar(int uangBayar) throws UangBayarKurangException {
        this.kembalian = hitungKembalian(uangBayar);
        this.bayar = uangBayar;
    }

    /** Billing yang dibeli dalam detik. */
    public int getDetik() {
        return durasiJam * 3600;
    }

    /** Dipanggil DAO setelah membaca tipe dan tarif terbaru komputer acuan dari database. */
    public void setTipeDanTarif(TipeKomputer tipe, int tarifPerJam) throws DataTidakValidException {
        this.tipe = tipe;
        this.tarifPerJam = tarifPerJam;
        this.total = hitungTotal();
    }

    public void setWaktu(LocalDateTime waktu) {
        this.waktu = waktu.withNano(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Pengguna getPetugas() {
        return petugas;
    }

    public Komputer getAcuan() {
        return acuan;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public TipeKomputer getTipe() {
        return tipe;
    }

    public int getDurasiJam() {
        return durasiJam;
    }

    public final void setDurasiJam(int durasiJam) throws DataTidakValidException {
        if (durasiJam < 1 || durasiJam > JAM_MAKS) {
            throw new DataTidakValidException("Billing yang dibeli harus 1 sampai " + JAM_MAKS + " jam");
        }
        this.durasiJam = durasiJam;
    }

    public int getTarifPerJam() {
        return tarifPerJam;
    }

    public int getTotal() {
        return total;
    }

    public int getBayar() {
        return bayar;
    }

    public int getKembalian() {
        return kembalian;
    }
}
