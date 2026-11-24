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
    nama      VARCHAR(50)  NOT NULL,
    role      ENUM('ADMIN', 'OPERATOR') NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS komputer (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nama           VARCHAR(30) NOT NULL UNIQUE,
    tipe           ENUM('REGULER', 'VIP') NOT NULL DEFAULT 'REGULER',
    tarif_per_jam  INT NOT NULL,
    status         ENUM('TERSEDIA', 'DIPAKAI', 'RUSAK') NOT NULL DEFAULT 'TERSEDIA',
    CONSTRAINT chk_komputer_tarif CHECK (tarif_per_jam > 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS sesi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    kode            VARCHAR(30) NOT NULL UNIQUE,
    id_komputer     INT NOT NULL,
    id_operator     INT NOT NULL,
    nama_pelanggan  VARCHAR(50) NOT NULL,
    waktu_mulai     DATETIME NOT NULL,
    waktu_selesai   DATETIME NULL COMMENT 'NULL selama sesi masih berjalan',
    durasi_menit    INT NOT NULL DEFAULT 0,
    tarif_per_jam   INT NOT NULL COMMENT 'salinan tarif saat sesi dimulai',
    total           INT NOT NULL DEFAULT 0,
    bayar           INT NOT NULL DEFAULT 0,
    kembalian       INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_sesi_komputer FOREIGN KEY (id_komputer) REFERENCES komputer (id),
    CONSTRAINT fk_sesi_operator FOREIGN KEY (id_operator) REFERENCES pengguna (id)
) ENGINE = InnoDB;

-- Data awal
-- Akun: admin / admin123  dan  operator / operator123
INSERT IGNORE INTO pengguna (username, password, nama, role) VALUES
    ('admin',    SHA2('admin123', 256),    'Administrator',  'ADMIN'),
    ('operator', SHA2('operator123', 256), 'Operator Warnet', 'OPERATOR');

INSERT IGNORE INTO komputer (nama, tipe, tarif_per_jam, status) VALUES
    ('PC-01',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-02',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-03',  'REGULER', 5000, 'TERSEDIA'),
    ('PC-04',  'REGULER', 5000, 'RUSAK'),
    ('VIP-01', 'VIP',     8000, 'TERSEDIA'),
    ('VIP-02', 'VIP',     8000, 'TERSEDIA');
