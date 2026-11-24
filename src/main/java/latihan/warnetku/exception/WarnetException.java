package latihan.warnetku.exception;

public class WarnetException extends Exception {
    private static final long serialVersionUID = 1L;

    public WarnetException(String pesan) {
        super(pesan);
    }

    public WarnetException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
