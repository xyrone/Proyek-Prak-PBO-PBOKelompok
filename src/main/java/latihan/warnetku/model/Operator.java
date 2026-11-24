package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

public class Operator extends Pengguna {
    public Operator(int id, String username, String passwordHash, String nama)
            throws DataTidakValidException {
        super(id, username, passwordHash, nama);
    }

    @Override
    public String getRole() {
        return "OPERATOR";
    }
}
