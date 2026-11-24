package latihan.warnetku.exception;

public class UangBayarKurangException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public UangBayarKurangException(String pesan) {
        super(pesan);
    }
}
