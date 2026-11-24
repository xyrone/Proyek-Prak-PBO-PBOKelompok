package latihan.warnetku.exception;

/** Username atau password salah / kosong. */
public class LoginGagalException extends WarnetException {
    private static final long serialVersionUID = 1L;

    public LoginGagalException(String pesan) {
        super(pesan);
    }
}
