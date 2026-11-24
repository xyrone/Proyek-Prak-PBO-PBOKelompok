package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.IsiBilling;

import java.util.List;

public interface IsiBillingDao {

    /**
     * Mencatat pembelian billing dan menambah billing pelanggan dalam satu transaksi.
     * Tarif komputer acuan dibaca ulang dari database sebelum uang bayar divalidasi.
     *
     * @throws latihan.warnetku.exception.UangBayarKurangException jika uang bayar kurang
     */
    void isi(IsiBilling isi, int uangBayar) throws WarnetException;

    /** Semua pembelian billing, terbaru lebih dulu. */
    List<IsiBilling> cariSemua() throws WarnetException;
}
