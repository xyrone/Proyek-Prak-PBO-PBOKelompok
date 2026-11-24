package latihan.warnetku.exception;

/** Komputer yang dipilih sedang dipakai atau rusak. */
public class KomputerTidakTersediaException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public KomputerTidakTersediaException(String pesan) {
        super(pesan);
    }
}
