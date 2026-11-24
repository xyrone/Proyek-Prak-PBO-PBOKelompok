package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.UangBayarKurangException;
import latihan.warnetku.exception.WarnetException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sesi {
    public static final int BLOK_MENIT = 15;
    private static final DateTimeFormatter FORMAT_KODE = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private int id;
    private final String kode;
    private final Komputer komputer;
    private final Pengguna operator;
    private String namaPelanggan;
    private final LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;
    private int tarifPerJam;
    private int durasiMenit;
    private int total;
    private int bayar;
    private int kembalian;

    public Sesi(Komputer komputer, Pengguna operator, String namaPelanggan)
            throws DataTidakValidException {
        if (komputer == null) {
            throw new DataTidakValidException("Komputer wajib dipilih");
        }
        if (operator == null) {
            throw new DataTidakValidException("Operator tidak diketahui");
        }
        this.komputer = komputer;
        this.operator = operator;
        setNamaPelanggan(namaPelanggan);
        LocalDateTime sekarang = LocalDateTime.now();
        this.waktuMulai = sekarang.withNano(0);
        this.tarifPerJam = komputer.getTarifPerJam();
        this.kode = "WN" + sekarang.format(FORMAT_KODE) + String.format("%03d", komputer.getId());
    }

    public Sesi(int id, String kode, Komputer komputer, Pengguna operator, String namaPelanggan,
                LocalDateTime waktuMulai, LocalDateTime waktuSelesai, int tarifPerJam,
                int durasiMenit, int total, int bayar, int kembalian) throws DataTidakValidException {
        this.id = id;
        this.kode = kode;
        this.komputer = komputer;
        this.operator = operator;
        setNamaPelanggan(namaPelanggan);
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.tarifPerJam = tarifPerJam;
        this.durasiMenit = durasiMenit;
        this.total = total;
        this.bayar = bayar;
        this.kembalian = kembalian;
    }

    public int hitungDurasiMenit() {
        return hitungDurasiMenit(waktuSelesai != null ? waktuSelesai : LocalDateTime.now());
    }

    public int hitungDurasiMenit(LocalDateTime sampai) {
        long detik = Math.max(0, Duration.between(waktuMulai, sampai).getSeconds());
        long menit = (detik + 59) / 60;
        long blok = Math.max(1, (menit + BLOK_MENIT - 1) / BLOK_MENIT);
        return (int) (blok * BLOK_MENIT);
    }

    public int hitungTotal() {
        return biaya(hitungDurasiMenit());
    }

    private int biaya(int menit) {
        return (int) Math.round((double) menit * tarifPerJam / 60.0);
    }

    public void selesai(LocalDateTime waktu) {
        this.waktuSelesai = waktu.withNano(0);
        this.durasiMenit = hitungDurasiMenit(this.waktuSelesai);
        this.total = biaya(durasiMenit);
        this.bayar = 0;
        this.kembalian = 0;
    }

    public int hitungKembalian(int uangBayar) throws UangBayarKurangException {
        if (uangBayar < total) {
            throw new UangBayarKurangException("Uang bayar kurang " + rupiah(total - uangBayar)
                    + " dari total " + rupiah(total));
        }
        return uangBayar - total;
    }

    public void bayar(int uangBayar) throws WarnetException {
        if (waktuSelesai == null) {
            throw new DataTidakValidException("Sesi belum diselesaikan");
        }
        this.kembalian = hitungKembalian(uangBayar);
        this.bayar = uangBayar;
    }

    private static String rupiah(int nilai) {
        return "Rp " + java.text.NumberFormat.getIntegerInstance(java.util.Locale.forLanguageTag("id-ID")).format(nilai);
    }

    public boolean isBerjalan() {
        return waktuSelesai == null;
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

    public Pengguna getOperator() {
        return operator;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public final void setNamaPelanggan(String namaPelanggan) throws DataTidakValidException {
        if (namaPelanggan == null || namaPelanggan.isBlank()) {
            throw new DataTidakValidException("Nama pelanggan wajib diisi");
        }
        if (namaPelanggan.trim().length() > 50) {
            throw new DataTidakValidException("Nama pelanggan maksimal 50 karakter");
        }
        this.namaPelanggan = namaPelanggan.trim();
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public int getTarifPerJam() {
        return tarifPerJam;
    }

    public final void setTarifPerJam(int tarifPerJam) {
        this.tarifPerJam = tarifPerJam;
    }

    public int getDurasiMenit() {
        return durasiMenit;
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
