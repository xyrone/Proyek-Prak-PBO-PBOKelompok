package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Pengguna;

import java.util.Optional;

public interface PenggunaDao {

    Optional<Pengguna> cariByUsername(String username) throws WarnetException;
}
