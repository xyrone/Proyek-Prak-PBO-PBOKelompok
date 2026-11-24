package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Pelanggan;

import java.util.Optional;

public interface PelangganDao extends CrudDao<Pelanggan> {

    /** Mencari berdasarkan ID pelanggan (kode), misalnya PLG-0001. */
    Optional<Pelanggan> cariByKode(String kode) throws WarnetException;
}
