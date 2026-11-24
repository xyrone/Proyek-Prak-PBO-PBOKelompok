package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

/** Satu unit komputer di warnet. Aturan validasi ada di setter. */
public class Komputer {

    private static final int PANJANG_NAMA_MAKS = 30;

    private int id;
    private String nama;
    private TipeKomputer tipe;
    private int tarifPerJam;
    private StatusKomputer status;

    /** Untuk data yang sudah ada di database. */
    public Komputer(int id, String nama, TipeKomputer tipe, int tarifPerJam, StatusKomputer status)
            throws DataTidakValidException {
        this.id = id;
        setNama(nama);
        setTipe(tipe);
        setTarifPerJam(tarifPerJam);
        setStatus(status);
    }

    /** Untuk komputer baru (belum punya id). */
    public Komputer(String nama, TipeKomputer tipe, int tarifPerJam, StatusKomputer status)
            throws DataTidakValidException {
        this(0, nama, tipe, tarifPerJam, status);
    }

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
            throw new DataTidakValidException("Nama komputer wajib diisi");
        }
        if (nama.trim().length() > PANJANG_NAMA_MAKS) {
            throw new DataTidakValidException("Nama komputer maksimal " + PANJANG_NAMA_MAKS + " karakter");
        }
        this.nama = nama.trim();
    }

    public TipeKomputer getTipe() {
        return tipe;
    }

    public final void setTipe(TipeKomputer tipe) throws DataTidakValidException {
        if (tipe == null) {
            throw new DataTidakValidException("Tipe komputer wajib dipilih");
        }
        this.tipe = tipe;
    }

    public int getTarifPerJam() {
        return tarifPerJam;
    }

    public final void setTarifPerJam(int tarifPerJam) throws DataTidakValidException {
        if (tarifPerJam <= 0) {
            throw new DataTidakValidException("Tarif per jam harus lebih dari 0");
        }
        this.tarifPerJam = tarifPerJam;
    }

    public StatusKomputer getStatus() {
        return status;
    }

    public final void setStatus(StatusKomputer status) throws DataTidakValidException {
        if (status == null) {
            throw new DataTidakValidException("Status komputer wajib dipilih");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return nama;
    }
}
