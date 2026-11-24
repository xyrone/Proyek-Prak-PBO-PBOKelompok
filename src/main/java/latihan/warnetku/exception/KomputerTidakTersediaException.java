package latihan.warnetku.exception;

public class KomputerTidakTersediaException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public KomputerTidakTersediaException(String pesan) {
        super(pesan);
    }
}
