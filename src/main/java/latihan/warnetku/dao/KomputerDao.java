package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Komputer;

import java.util.List;

public interface KomputerDao extends CrudDao<Komputer> {
    List<Komputer> cariTersedia() throws WarnetException;
}
