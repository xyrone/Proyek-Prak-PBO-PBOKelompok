package latihan.warnetku.model;

import latihan.warnetku.exception.BillingKurangException;
import latihan.warnetku.exception.DataTidakValidException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Satu sesi pemakaian komputer oleh pelanggan. Lama main dipotong dari billing pelanggan saat
 * sesi dimulai. Jika sesi diakhiri sebelum waktunya, sisa waktu dikembalikan ke billing.
 */
public class Sesi {

    public static final int MENIT_MAKS = 24 * 60;
    private static final DateTimeFormatter FORMAT_KODE = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private int id;
    private String kode;
    private final Komputer komputer;
    private final Pengguna petugas;
    private final Pelanggan pelanggan;
    private LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;
    private int durasiMenit;
    private int billingKembaliDetik;

    /** Sesi baru. Waktu mulai yang sebenarnya dicatat saat DAO menyimpannya (lihat {@link #mulaiPada}). */
    public Sesi(Komputer komputer, Pengguna petugas, Pelanggan pelanggan, int durasiMenit)
            throws DataTidakValidException {
        if (komputer == null) {
            throw new DataTidakValidException("Komputer wajib dipilih");
        }
        if (petugas == null) {
            throw new DataTidakValidException("Petugas tidak diketahui");
        }
        if (pelanggan == null) {
            throw new DataTidakValidException("Pelanggan wajib dipilih");
        }
        this.komputer = komputer;
        this.petugas = petugas;
        this.pelanggan = pelanggan;
        setDurasiMenit(durasiMenit);
        mulaiPada(LocalDateTime.now());
    }

    /** Sesi yang dibaca dari database. */
    public Sesi(int id, String kode, Komputer komputer, Pengguna petugas, Pelanggan pelanggan,
                LocalDateTime waktuMulai, LocalDateTime waktuSelesai, int durasiMenit, int billingKembaliDetik) {
        this.id = id;
        this.kode = kode;
        this.komputer = komputer;
        this.petugas = petugas;
        this.pelanggan = pelanggan;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.durasiMenit = durasiMenit;
        this.billingKembaliDetik = billingKembaliDetik;
    }

    /** Memastikan billing cukup untuk lama main sesi ini. */
    public final void cekBilling(int billingDetik) throws BillingKurangException {
        if (billingDetik < getDurasiDetik()) {
            throw new BillingKurangException("Billing " + pelanggan.getNama() + " untuk komputer "
                    + komputer.getTipe() + " hanya " + formatLama(billingDetik) + ", kurang dari "
                    + formatLama(getDurasiDetik()) + ". Kurangi lama main atau isi billing dulu.");
        }
    }

    /** Menetapkan saat sesi benar-benar dimulai; kode sesi dibuat dari waktu ini. */
    public final void mulaiPada(LocalDateTime waktu) {
        this.waktuMulai = waktu.withNano(0);
        this.kode = "WN" + waktu.format(FORMAT_KODE) + String.format("%03d", komputer.getId());
    }

    public int getDurasiDetik() {
        return durasiMenit * 60;
    }

    /** Waktu jatah main habis (waktu mulai + lama main). */
    public LocalDateTime getWaktuBerakhir() {
        return waktuMulai.plusMinutes(durasiMenit);
    }

    /** Sisa jatah main dalam detik; 0 jika sudah habis. */
    public long hitungSisaDetik(LocalDateTime sekarang) {
        return Math.max(0, Duration.between(sekarang, getWaktuBerakhir()).getSeconds());
    }

    public boolean isHabis(LocalDateTime sekarang) {
        return hitungSisaDetik(sekarang) == 0;
    }

    /** Menutup sesi. Sisa waktu pada saat ini dikembalikan ke billing pelanggan. */
    public void selesai(LocalDateTime waktu) {
        this.waktuSelesai = waktu.withNano(0);
        this.billingKembaliDetik = (int) hitungSisaDetik(waktuSelesai);
    }

    public boolean isBerjalan() {
        return waktuSelesai == null;
    }

    /** Contoh: 5400 detik menjadi "1 jam 30 menit". */
    public static String formatLama(long detik) {
        long menit = detik / 60;
        long jam = menit / 60;
        long sisaMenit = menit % 60;
        if (jam == 0) {
            return sisaMenit + " menit";
        }
        return sisaMenit == 0 ? jam + " jam" : jam + " jam " + sisaMenit + " menit";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public Komputer getKomputer() {
        return komputer;
    }

    public Pengguna getPetugas() {
        return petugas;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public int getDurasiMenit() {
        return durasiMenit;
    }

    public final void setDurasiMenit(int durasiMenit) throws DataTidakValidException {
        if (durasiMenit < 1 || durasiMenit > MENIT_MAKS) {
            throw new DataTidakValidException("Lama main harus 1 menit sampai 24 jam");
        }
        this.durasiMenit = durasiMenit;
    }

    public int getBillingKembaliDetik() {
        return billingKembaliDetik;
    }
}
