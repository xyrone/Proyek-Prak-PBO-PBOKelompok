package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;
import latihan.warnetku.model.Sesi;

import java.util.List;

public interface SesiDao {
    void mulai(Sesi sesi) throws WarnetException;

    void selesaikan(Sesi sesi, int uangBayar) throws WarnetException;

    List<Sesi> cariBerjalan() throws WarnetException;

    List<Sesi> cariSelesai() throws WarnetException;
}
