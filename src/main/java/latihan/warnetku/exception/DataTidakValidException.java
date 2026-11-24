package latihan.warnetku.exception;

public class DataTidakValidException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public DataTidakValidException(String pesan) {
        super(pesan);
    }
}
