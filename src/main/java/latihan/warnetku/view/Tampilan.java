package latihan.warnetku.view;

import latihan.warnetku.exception.DataTidakValidException;
import latihan.warnetku.exception.WarnetException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

/** Kumpulan pembantu tampilan: warna, format teks, dialog pesan, dan gaya tabel. */
final class Tampilan {

    static final Color BIRU = new Color(0x1F4E8C);
    static final Color BIRU_MUDA = new Color(0xE8F0FA);
    static final Color HIJAU = new Color(0x1B7F3B);
    static final Color MERAH = new Color(0xB3261E);
    static final Color ORANYE = new Color(0xB35900);
    static final Color ZEBRA = new Color(0xF4F7FB);
    static final Color ABU = new Color(0x5F6B7A);

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter TANGGAL_JAM = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", ID);
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm:ss", ID);

    private Tampilan() {
    }

    /** Aksi yang boleh melempar kesalahan aplikasi; kesalahannya ditampilkan sebagai dialog. */
    @FunctionalInterface
    interface Aksi {
        void jalankan() throws WarnetException;
    }

    static void coba(Component induk, Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (WarnetException e) {
            error(induk, e.getMessage());
        }
    }

    static void error(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean konfirmasi(Component induk, String pesan) {
        return JOptionPane.showConfirmDialog(induk, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ---------- format teks ----------

    static String rupiah(int nilai) {
        return "Rp " + NumberFormat.getIntegerInstance(ID).format(nilai);
    }

    static String waktu(LocalDateTime t) {
        return t == null ? "-" : t.format(TANGGAL_JAM);
    }

    static String jam(LocalDateTime t) {
        return t == null ? "-" : t.format(JAM);
    }

    static String lama(int jam) {
        return jam + " jam";
    }

    /** Contoh: 5400 detik menjadi "1 jam 30 menit". */
    static String lamaDetik(long detik) {
        return latihan.warnetku.model.Sesi.formatLama(detik);
    }

    static String jamMenitDetik(Duration d) {
        long detik = Math.max(0, d.getSeconds());
        return String.format("%02d:%02d:%02d", detik / 3600, (detik % 3600) / 60, detik % 60);
    }

    /** Mengubah teks seperti "5.000" atau "Rp 5000" menjadi angka bulat. */
    static int parseAngka(String teks, String namaField) throws DataTidakValidException {
        String bersih = teks == null ? "" : teks.replace("Rp", "").replace(".", "").replace(",", "").trim();
        if (bersih.isEmpty()) {
            throw new DataTidakValidException(namaField + " wajib diisi");
        }
        try {
            return Integer.parseInt(bersih);
        } catch (NumberFormatException e) {
            throw new DataTidakValidException(namaField + " harus berupa angka bulat");
        }
    }

    // ---------- komponen ----------

    static JLabel judul(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 16f));
        return l;
    }

    static JLabel keterangan(String teks) {
        JLabel l = new JLabel(teks);
        l.setForeground(ABU);
        return l;
    }

    static JButton tombol(String teks) {
        JButton b = new JButton(teks);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(Math.max(100, b.getPreferredSize().width + 16), 32));
        return b;
    }

    // ---------- tabel ----------

    static void rapikanTabel(JTable tabel, int... kolomKanan) {
        tabel.setRowHeight(28);
        tabel.setFillsViewportHeight(true);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setFont(tabel.getTableHeader().getFont().deriveFont(Font.BOLD));
        tabel.setShowGrid(false);
        tabel.setIntercellSpacing(new Dimension(0, 0));
        tabel.setDefaultRenderer(Object.class, new RenderTeks(SwingConstants.LEFT, null));
        for (int kolom : kolomKanan) {
            tabel.getColumnModel().getColumn(kolom)
                    .setCellRenderer(new RenderTeks(SwingConstants.RIGHT, null));
        }
    }

    /** Sel tabel dengan garis belang, rata kiri/kanan, dan warna teks opsional per nilai. */
    static final class RenderTeks extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final int rata;
        private final transient Function<Object, Color> warna;

        RenderTeks(int rata, Function<Object, Color> warna) {
            this.rata = rata;
            this.warna = warna;
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai, boolean terpilih,
                                                       boolean fokus, int baris, int kolom) {
            Component sel = super.getTableCellRendererComponent(tabel, nilai, terpilih, fokus, baris, kolom);
            setHorizontalAlignment(rata);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
            if (!terpilih) {
                sel.setBackground(baris % 2 == 0 ? Color.WHITE : ZEBRA);
                sel.setForeground(warna == null ? Color.BLACK : warna.apply(nilai));
            }
            return sel;
        }
    }
}
