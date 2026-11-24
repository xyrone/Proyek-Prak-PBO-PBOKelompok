package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

public class Admin extends Pengguna {
    public Admin(int id, String username, String passwordHash, String nama)
            throws DataTidakValidException {
        super(id, username, passwordHash, nama);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
