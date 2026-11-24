package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

/**
 * Induk bagi semua orang yang tercatat di Warnetku: petugas ({@link Pengguna}) dan
 * pelanggan ({@link Pelanggan}). Menampung data dan aturan yang sama, yaitu id dan nama.
 */
public abstract class Orang {

    public static final int NAMA_MAKS = 50;

    private int id;
    private String nama;

    protected Orang(int id, String nama) throws DataTidakValidException {
        this.id = id;
        setNama(nama);
    }

    /** Sebutan peran untuk pesan kesalahan dan tampilan, misalnya "petugas" atau "pelanggan". */
    public abstract String getPeran();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public final void setNama(String nama) throws DataTidakValidException {
        if (nama == null || nama.isBlank()) {
            throw new DataTidakValidException("Nama " + getPeran() + " wajib diisi");
        }
        if (nama.trim().length() > NAMA_MAKS) {
            throw new DataTidakValidException("Nama " + getPeran() + " maksimal " + NAMA_MAKS + " karakter");
        }
        this.nama = nama.trim();
    }

    @Override
    public String toString() {
        return nama;
    }
}
