package latihan.warnetku.exception;

/**
 * Induk semua kesalahan aplikasi (checked exception).
 * Pesannya sudah berbahasa pengguna sehingga bisa langsung ditampilkan di JOptionPane.
 */
public class WarnetException extends Exception {
    private static final long serialVersionUID = 1L;


    public WarnetException(String pesan) {
        super(pesan);
    }

    public WarnetException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
