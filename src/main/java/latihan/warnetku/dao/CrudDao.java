package latihan.warnetku.dao;

import latihan.warnetku.exception.WarnetException;

import java.util.List;
import java.util.Optional;

/** Operasi dasar simpan, ubah, hapus, dan cari untuk satu jenis data. */
public interface CrudDao<T> {

    void simpan(T data) throws WarnetException;

    void ubah(T data) throws WarnetException;

    void hapus(int id) throws WarnetException;

    List<T> cariSemua() throws WarnetException;

    Optional<T> cariById(int id) throws WarnetException;
}
