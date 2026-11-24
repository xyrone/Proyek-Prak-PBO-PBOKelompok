package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public abstract class Pengguna {
    private final int id;
    private String username;
    private final String password;
    private String nama;

    protected Pengguna(int id, String username, String passwordHash, String nama)
            throws DataTidakValidException {
        this.id = id;
        this.password = passwordHash;
        setUsername(username);
        setNama(nama);
    }

    public abstract String getRole();

    public boolean cocokPassword(String passwordTeks) {
        return passwordTeks != null && password != null && password.equals(hash(passwordTeks));
    }

    public static String hash(String teks) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(teks.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public final void setUsername(String username) throws DataTidakValidException {
        if (username == null || username.isBlank()) {
            throw new DataTidakValidException("Username wajib diisi");
        }
        this.username = username.trim();
    }

    public String getNama() {
        return nama;
    }

    public final void setNama(String nama) throws DataTidakValidException {
        if (nama == null || nama.isBlank()) {
            throw new DataTidakValidException("Nama pengguna wajib diisi");
        }
        this.nama = nama.trim();
    }
}
