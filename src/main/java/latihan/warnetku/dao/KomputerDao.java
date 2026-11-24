package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;

import java.util.List;

public interface KomputerDao extends CrudDao<Komputer> {

    /** Komputer berstatus TERSEDIA, siap dipakai sesi baru. */
    List<Komputer> cariTersedia() throws WarnetException;
}
