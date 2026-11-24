package latihan.warnetku.exception;

/** Sisa billing pelanggan tidak cukup untuk lama main yang diminta. */
public class BillingKurangException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public BillingKurangException(String pesan) {
        super(pesan);
    }
}
