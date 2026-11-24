package latihan.warnetku.exception;

/** Uang bayar kurang dari total biaya. */
public class UangBayarKurangException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public UangBayarKurangException(String pesan) {
        super(pesan);
    }
}
