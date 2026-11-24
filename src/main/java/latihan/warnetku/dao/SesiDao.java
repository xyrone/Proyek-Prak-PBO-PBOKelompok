package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Sesi;

import java.util.List;

public interface SesiDao {

    /**
     * Memulai sesi. Dalam satu transaksi: billing pelanggan dicek lalu dipotong, komputer harus
     * TERSEDIA lalu menjadi DIPAKAI, dan baris sesi dibuat. Tidak ada pembayaran di sini karena
     * billing sudah dibayar saat diisi.
     *
     * @throws latihan.warnetku.exception.BillingKurangException jika billing tidak cukup
     * @throws latihan.warnetku.exception.KomputerTidakTersediaException jika komputer sedang dipakai/rusak
     */
    void mulai(Sesi sesi) throws WarnetException;

    /**
     * Menutup sesi: waktu selesai dicatat, sisa waktu dikembalikan ke billing pelanggan, dan
     * komputer kembali TERSEDIA dalam satu transaksi.
     */
    void selesaikan(Sesi sesi) throws WarnetException;

    /** Sesi yang belum ditutup, yang terlama dimulai lebih dulu. */
    List<Sesi> cariBerjalan() throws WarnetException;

    /** Semua sesi (berjalan dan selesai), terbaru lebih dulu. */
    List<Sesi> cariSemua() throws WarnetException;
}
