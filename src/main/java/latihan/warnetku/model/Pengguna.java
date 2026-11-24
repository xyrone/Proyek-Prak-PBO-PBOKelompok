package latihan.warnetku.model;

import latihan.warnetku.exception.DataTidakValidException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Akun petugas warnet. Satu jenis akun untuk semua fitur (tanpa pemisahan peran). */
public class Pengguna extends Orang {

    private String username;
    private final String password; // hash SHA-256 (hex), bukan teks asli

    public Pengguna(int id, String username, String passwordHash, String nama)
            throws DataTidakValidException {
        super(id, nama);
        this.password = passwordHash;
        setUsername(username);
    }

    /** Membandingkan password yang diketik dengan hash yang tersimpan. */
    public boolean cocokPassword(String passwordTeks) {
        return passwordTeks != null && password != null && password.equals(hash(passwordTeks));
    }

    public static String hash(String teks) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(teks.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e); // selalu ada di JDK
        }
    }

    @Override
    public String getPeran() {
        return "petugas";
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
}
