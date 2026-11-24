-- Warnetku - skema database dan data awal
-- Jalankan di MySQL (Laragon): mysql -u root < database/schema.sql
-- Aman dijalankan berulang kali: tabel dan data awal tidak ditimpa.

CREATE DATABASE IF NOT EXISTS warnetku
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE warnetku;

CREATE TABLE IF NOT EXISTS pengguna (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(30)  NOT NULL UNIQUE,
    password  VARCHAR(64)  NOT NULL COMMENT 'hash SHA-256 (hex)',
    nama      VARCHAR(50)  NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS komputer (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nama           VARCHAR(30) NOT NULL UNIQUE,
    tipe           ENUM('REGULER', 'VIP') NOT NULL DEFAULT 'REGULER',
    tarif_per_jam  INT NOT NULL,
    status         ENUM('TERSEDIA', 'DIPAKAI', 'RUSAK') NOT NULL DEFAULT 'TERSEDIA',
    CONSTRAINT chk_komputer_tarif CHECK (tarif_per_jam > 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pelanggan (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    kode              VARCHAR(20) NOT NULL UNIQUE COMMENT 'ID pelanggan yang diketik petugas, contoh PLG-0001',
    nama              VARCHAR(50) NOT NULL,
    billing_reguler   INT NOT NULL DEFAULT 0 COMMENT 'sisa billing komputer REGULER (detik)',
    billing_vip       INT NOT NULL DEFAULT 0 COMMENT 'sisa billing komputer VIP (detik)',
    dibuat            DATETIME NOT NULL,
    CONSTRAINT chk_pelanggan_billing CHECK (billing_reguler >= 0 AND billing_vip >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS isi_billing (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan    INT NOT NULL,
    id_petugas      INT NOT NULL,
    waktu           DATETIME NOT NULL,
    tipe            ENUM('REGULER', 'VIP') NOT NULL,
    durasi_jam      INT NOT NULL COMMENT 'billing yang dibeli',
    tarif_per_jam   INT NOT NULL,
    total           INT NOT NULL COMMENT 'durasi_jam x tarif_per_jam',
    bayar           INT NOT NULL,
    kembalian       INT NOT NULL,
    CONSTRAINT chk_isi_durasi CHECK (durasi_jam BETWEEN 1 AND 24),
    CONSTRAINT chk_isi_bayar CHECK (bayar >= total),
    CONSTRAINT fk_isi_pelanggan FOREIGN KEY (id_pelanggan) REFERENCES pelanggan (id),
    CONSTRAINT fk_isi_petugas FOREIGN KEY (id_petugas) REFERENCES pengguna (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS sesi (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    kode                    VARCHAR(30) NOT NULL UNIQUE,
    id_komputer             INT NOT NULL,
    id_petugas              INT NOT NULL,
    id_pelanggan            INT NOT NULL,
    waktu_mulai             DATETIME NOT NULL,
    durasi_menit            INT NOT NULL COMMENT 'lama main, dipotong dari billing pelanggan',
    waktu_selesai           DATETIME NULL COMMENT 'NULL selama sesi masih berjalan',
    billing_kembali_detik   INT NULL COMMENT 'sisa waktu yang dikembalikan ke billing saat sesi diakhiri',
    CONSTRAINT chk_sesi_durasi CHECK (durasi_menit BETWEEN 1 AND 1440),
    CONSTRAINT fk_sesi_komputer FOREIGN KEY (id_komputer) REFERENCES komputer (id),
    CONSTRAINT fk_sesi_petugas FOREIGN KEY (id_petugas) REFERENCES pengguna (id),
    CONSTRAINT fk_sesi_pelanggan FOREIGN KEY (id_pelanggan) REFERENCES pelanggan (id)
) ENGINE = InnoDB;

-- Data awal
-- Satu akun petugas untuk semua fitur: admin / admin123
INSERT IGNORE INTO pengguna (username, password, nama) VALUES
    ('admin', SHA2('admin123', 256), 'Petugas Warnet');

INSERT IGNORE INTO komputer (nama, tipe, tarif_per_jam, status) VALUES
    ('PC-01',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-02',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-03',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-04',  'REGULER', 5000, 'RUSAK'),
    ('VIP-01', 'VIP',     8000, 'TERSEDIA'),
    ('VIP-02', 'VIP',     8000, 'TERSEDIA');

INSERT IGNORE INTO pelanggan (kode, nama, billing_reguler, billing_vip, dibuat) VALUES
    ('PLG-0001', 'Pelanggan Contoh', 7200, 0, NOW());
