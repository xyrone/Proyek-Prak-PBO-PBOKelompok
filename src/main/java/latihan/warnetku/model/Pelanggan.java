package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

import java.time.LocalDateTime;

/**
 * Pelanggan warnet (turunan {@link Orang}) dengan ID (kode) dan sisa billing. Billing adalah sisa waktu main yang
 * sudah dibayar, dicatat dalam detik terpisah untuk komputer REGULER dan VIP karena tarifnya berbeda.
 */
public class Pelanggan extends Orang {

    public static final String AWALAN_KODE = "PLG-";
    /** Batas billing tersimpan per tipe supaya angka tidak membengkak. */
    public static final int BILLING_MAKS_DETIK = 100 * 3600;

    private String kode;
    private int billingRegulerDetik;
    private int billingVipDetik;
    private final LocalDateTime dibuat;

    /** Pelanggan baru; kode dibuat oleh DAO setelah id diketahui. */
    public Pelanggan(String nama) throws DataTidakValidException {
        super(0, nama);
        this.dibuat = LocalDateTime.now().withNano(0);
    }

    /** Pelanggan dari database. */
    public Pelanggan(int id, String kode, String nama, int billingRegulerDetik, int billingVipDetik,
                     LocalDateTime dibuat) throws DataTidakValidException {
        super(id, nama);
        this.kode = kode;
        this.billingRegulerDetik = billingRegulerDetik;
        this.billingVipDetik = billingVipDetik;
        this.dibuat = dibuat;
    }

    /** Merapikan ID yang diketik petugas: "plg-1", "PLG-0001", atau "1" menjadi PLG-0001. */
    public static String normalKode(String teks) throws DataTidakValidException {
        String t = teks == null ? "" : teks.trim().toUpperCase();
        if (t.isEmpty()) {
            throw new DataTidakValidException("ID pelanggan wajib diisi");
        }
        if (t.startsWith(AWALAN_KODE)) {
            t = t.substring(AWALAN_KODE.length());
        }
        if (!t.matches("\\d{1,8}")) {
            throw new DataTidakValidException("Format ID pelanggan salah. Contoh: PLG-0001 atau 1");
        }
        return buatKode(Integer.parseInt(t));
    }

    public static String buatKode(int nomor) {
        return AWALAN_KODE + String.format("%04d", nomor);
    }

    public int getBillingDetik(TipeKomputer tipe) {
        return tipe == TipeKomputer.VIP ? billingVipDetik : billingRegulerDetik;
    }

    public void setBillingDetik(TipeKomputer tipe, int detik) {
        if (tipe == TipeKomputer.VIP) {
            billingVipDetik = detik;
        } else {
            billingRegulerDetik = detik;
        }
    }
    
    public int getBillingRegulerDetik() {
        return billingRegulerDetik;
    }

    public void setBillingRegulerDetik(int billingRegulerDetik) {
        this.billingRegulerDetik = billingRegulerDetik;
    }

    public int getBillingVipDetik() {
        return billingVipDetik;
    }

    public void setBillingVipDetik(int billingVipDetik) {
        this.billingVipDetik = billingVipDetik;
    }

    @Override
    public String getPeran() {
        return "pelanggan";
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public LocalDateTime getDibuat() {
        return dibuat;
    }

    @Override
    public String toString() {
        return kode + " - " + getNama();
    }
}
