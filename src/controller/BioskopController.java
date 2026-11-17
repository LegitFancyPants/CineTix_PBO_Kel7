package controller;

import dao.DatabaseConnection;
import dao.FilmDAO;
import dao.TransaksiDAO;
import exception.TiketInvalidException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Film;
import model.Tiket;
import model.TiketPremiere;
import model.TiketReguler;
import model.TiketVIP;
import model.Transaksi;

/**
 * Kelas Controller utama yang menjembatani antarmuka pengguna (View) dengan
 * lapisan akses data (DAO) dan model bisnis (Model).
 * Menerapkan pemisahan MVC ketat, penyimpanan cache menggunakan Map<String, Film>,
 * serta polimorfisme pada pembuatan dan perhitungan harga tiket.
 */
public class BioskopController {
    private FilmDAO filmDAO;
    private TransaksiDAO transaksiDAO;
    private Map<String, Film> cacheFilm;

    public BioskopController() {
        DatabaseConnection.inisialisasiTabel();
        this.filmDAO = new FilmDAO();
        this.transaksiDAO = new TransaksiDAO(this.filmDAO);
        this.cacheFilm = new HashMap<>();
        segarkanCacheFilm();
    }

    /**
     * Memperbarui isi Map cacheFilm dari database SQLite.
     */
    private void segarkanCacheFilm() {
        cacheFilm.clear();
        List<Film> semuaFilm = filmDAO.ambilSemua();
        for (Film f : semuaFilm) {
            cacheFilm.put(f.getIdFilm().toUpperCase(), f);
        }
    }

    /**
     * Menambahkan data film baru setelah memvalidasi input dan keunikan ID Film.
     */
    public void tambahFilm(String id, String judul, String genre, int durasi, String rating, double harga)
            throws TiketInvalidException {
        Film filmBaru = new Film(id, judul, genre, durasi, rating, harga);
        segarkanCacheFilm();
        if (cacheFilm.containsKey(filmBaru.getIdFilm().toUpperCase())) {
            throw new TiketInvalidException("ID Film '" + filmBaru.getIdFilm() + "' sudah terdaftar! Gunakan ID lain.");
        }
        try {
            filmDAO.simpan(filmBaru);
            cacheFilm.put(filmBaru.getIdFilm().toUpperCase(), filmBaru);
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal menyimpan film: " + e.getMessage(), e);
        }
    }

    /**
     * Mengubah data film yang sudah ada di database dan memperbarui cache.
     */
    public void ubahFilm(String id, String judul, String genre, int durasi, String rating, double harga)
            throws TiketInvalidException {
        Film filmUbah = new Film(id, judul, genre, durasi, rating, harga);
        segarkanCacheFilm();
        if (!cacheFilm.containsKey(filmUbah.getIdFilm().toUpperCase())) {
            throw new TiketInvalidException("ID Film '" + filmUbah.getIdFilm() + "' tidak ditemukan di database!");
        }
        try {
            filmDAO.ubah(filmUbah);
            cacheFilm.put(filmUbah.getIdFilm().toUpperCase(), filmUbah);
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal mengubah data film: " + e.getMessage(), e);
        }
    }

    /**
     * Menghapus data film berdasarkan ID Film.
     */
    public void hapusFilm(String idFilm) throws TiketInvalidException {
        if (idFilm == null || idFilm.trim().isEmpty()) {
            throw new TiketInvalidException("Pilih atau masukkan ID Film yang ingin dihapus!");
        }
        String key = idFilm.trim().toUpperCase();
        segarkanCacheFilm();
        if (!cacheFilm.containsKey(key)) {
            throw new TiketInvalidException("ID Film '" + idFilm.trim() + "' tidak ditemukan di database!");
        }
        try {
            filmDAO.hapus(idFilm.trim());
            cacheFilm.remove(key);
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal menghapus film: " + e.getMessage(), e);
        }
    }

    /**
     * Mengambil seluruh daftar film dari database sekaligus memperbarui cache Map.
     */
    public List<Film> getDaftarFilm() {
        List<Film> daftar = filmDAO.ambilSemua();
        cacheFilm.clear();
        for (Film f : daftar) {
            cacheFilm.put(f.getIdFilm().toUpperCase(), f);
        }
        return daftar;
    }

    /**
     * Mencari film berdasarkan kata kunci judul, ID, atau genre.
     */
    public List<Film> cariFilm(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDaftarFilm();
        }
        return filmDAO.cariBerdasarkanJudul(keyword);
    }

    /**
     * Mengambil objek Film secara cepat dari cache Map berdasarkan idFilm.
     */
    public Film getFilmDariCache(String idFilm) {
        if (idFilm == null) {
            return null;
        }
        Film film = cacheFilm.get(idFilm.trim().toUpperCase());
        if (film == null) {
            segarkanCacheFilm();
            film = cacheFilm.get(idFilm.trim().toUpperCase());
        }
        return film;
    }

    public Map<String, Film> getCacheFilm() {
        return cacheFilm;
    }

    /**
     * Factory Method polimorfik untuk membuat instansiasi subclass Tiket
     * (TiketReguler, TiketVIP, atau TiketPremiere) yang direferensikan oleh tipe induk Tiket.
     */
    public Tiket buatTiket(String kelas, String kode, Film film, String hari, String kursi)
            throws TiketInvalidException {
        return buatTiket(kelas, kode, film, hari, kursi, true);
    }

    /**
     * Overloaded Factory Method polimorfik dengan opsi fasilitas tambahan (bonusSnack / fasilitasSelimut).
     */
    public Tiket buatTiket(String kelas, String kode, Film film, String hari, String kursi, boolean fasilitasTambahan)
            throws TiketInvalidException {
        if (kelas == null || kelas.trim().isEmpty()) {
            throw new TiketInvalidException("Kelas layanan tiket harus dipilih!");
        }
        String k = kelas.trim();
        if ("Reguler".equalsIgnoreCase(k)) {
            return new TiketReguler(kode, film, hari, kursi);
        } else if ("VIP".equalsIgnoreCase(k)) {
            return new TiketVIP(kode, film, hari, kursi, fasilitasTambahan);
        } else if ("Premiere".equalsIgnoreCase(k)) {
            return new TiketPremiere(kode, film, hari, kursi, fasilitasTambahan);
        } else {
            throw new TiketInvalidException("Kelas studio tidak valid: " + kelas);
        }
    }

    /**
     * Menyimpan transaksi baru ke database SQLite setelah melalui validasi model.
     */
    public void simpanTransaksi(String id, String pembeli, String tgl, Tiket tiket, int jml, boolean member)
            throws TiketInvalidException {
        Transaksi trxBaru = new Transaksi(id, pembeli, tgl, tiket, jml, member);
        for (Transaksi existing : transaksiDAO.ambilSemua()) {
            if (existing.getIdTransaksi().equalsIgnoreCase(trxBaru.getIdTransaksi())) {
                throw new TiketInvalidException(
                        "ID Transaksi '" + trxBaru.getIdTransaksi() + "' sudah terdaftar! Gunakan ID lain.");
            }
        }
        try {
            transaksiDAO.simpan(trxBaru);
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal menyimpan transaksi: " + e.getMessage(), e);
        }
    }

    /**
     * Mengubah data transaksi yang sudah ada di database SQLite.
     */
    public void ubahTransaksi(String id, String pembeli, String tgl, Tiket tiket, int jml, boolean member)
            throws TiketInvalidException {
        Transaksi trxUbah = new Transaksi(id, pembeli, tgl, tiket, jml, member);
        boolean ditemukan = false;
        for (Transaksi existing : transaksiDAO.ambilSemua()) {
            if (existing.getIdTransaksi().equalsIgnoreCase(trxUbah.getIdTransaksi())) {
                ditemukan = true;
                break;
            }
        }
        if (!ditemukan) {
            throw new TiketInvalidException("ID Transaksi '" + trxUbah.getIdTransaksi() + "' tidak ditemukan!");
        }
        try {
            transaksiDAO.ubah(trxUbah);
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal mengubah transaksi: " + e.getMessage(), e);
        }
    }

    /**
     * Menghapus data transaksi berdasarkan ID Transaksi.
     */
    public void hapusTransaksi(String idTransaksi) throws TiketInvalidException {
        if (idTransaksi == null || idTransaksi.trim().isEmpty()) {
            throw new TiketInvalidException("Pilih atau masukkan ID Transaksi yang ingin dihapus!");
        }
        try {
            transaksiDAO.hapus(idTransaksi.trim());
        } catch (RuntimeException e) {
            throw new TiketInvalidException("Gagal menghapus transaksi: " + e.getMessage(), e);
        }
    }

    /**
     * Mengambil seluruh daftar riwayat transaksi dari database SQLite.
     */
    public List<Transaksi> getDaftarTransaksi() {
        return transaksiDAO.ambilSemua();
    }

    /**
     * Mencari transaksi berdasarkan kata kunci nama pembeli, ID transaksi, atau judul film.
     */
    public List<Transaksi> cariTransaksi(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDaftarTransaksi();
        }
        return transaksiDAO.cariBerdasarkanPembeli(keyword);
    }

    /**
     * Menghitung estimasi total bayar secara polimorfik (Overriding + Overloading)
     * untuk ditampilkan sebagai pratinjau real-time pada TransaksiPanel.
     */
    public double hitungEstimasiTotal(String kelas, Film film, String hari, int jumlah, boolean memberVip) {
        if (film == null || jumlah <= 0 || kelas == null || hari == null) {
            return 0.0;
        }
        try {
            Tiket tiketPreview = buatTiket(kelas, "PREVIEW", film, hari, "A1");
            Transaksi trxPreview = new Transaksi("PREVIEW", "Preview", "01/01/2026", tiketPreview, jumlah, memberVip);
            return memberVip ? trxPreview.hitungTotalBayar(0.10) : trxPreview.hitungTotalBayar();
        } catch (TiketInvalidException e) {
            return 0.0;
        }
    }

    /**
     * Menghitung harga satuan tiket secara polimorfik untuk informasi detail di View.
     */
    public double hitungHargaSatuan(String kelas, Film film, String hari) {
        if (film == null || kelas == null || hari == null) {
            return 0.0;
        }
        try {
            Tiket tiketPreview = buatTiket(kelas, "PREVIEW", film, hari, "A1");
            return tiketPreview.hitungHarga();
        } catch (TiketInvalidException e) {
            return 0.0;
        }
    }

    /**
     * Memvalidasi kredensial login petugas/admin pada LoginPanel.
     */
    public boolean validasiLogin(String username, String password) throws TiketInvalidException {
        if (username == null || username.trim().isEmpty()) {
            throw new TiketInvalidException("Username tidak boleh kosong!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new TiketInvalidException("Password tidak boleh kosong!");
        }
        String u = username.trim();
        String p = password.trim();
        if ((u.equalsIgnoreCase("admin") && p.equals("admin123"))
                || (u.equalsIgnoreCase("kasir") && p.equals("kasir123"))) {
            return true;
        }
        throw new TiketInvalidException("Username atau password salah! Gunakan akun: admin / admin123");
    }

    /**
     * Menghasilkan ID Film otomatis berikutnya (contoh: FLM-05).
     */
    public String generateIdFilmOtomatis() {
        List<Film> daftar = getDaftarFilm();
        int maxNomor = 0;
        for (Film f : daftar) {
            String id = f.getIdFilm().toUpperCase();
            if (id.startsWith("FLM-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxNomor) {
                        maxNomor = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("FLM-%02d", maxNomor + 1);
    }

    /**
     * Menghasilkan ID Transaksi otomatis berikutnya (contoh: TRX-003).
     */
    public String generateIdTransaksiOtomatis() {
        List<Transaksi> daftar = getDaftarTransaksi();
        int maxNomor = 0;
        for (Transaksi t : daftar) {
            String id = t.getIdTransaksi().toUpperCase();
            if (id.startsWith("TRX-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxNomor) {
                        maxNomor = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("TRX-%03d", maxNomor + 1);
    }

    public int getTotalJudulFilmAktif() {
        return getDaftarFilm().size();
    }

    public int getTotalTransaksi() {
        return getDaftarTransaksi().size();
    }

    public int getTotalTiketTerjual() {
        int total = 0;
        for (Transaksi t : getDaftarTransaksi()) {
            total += t.getJumlahTiket();
        }
        return total;
    }

    public double getTotalPendapatan() {
        double total = 0.0;
        for (Transaksi t : getDaftarTransaksi()) {
            total += t.getTotalBayar();
        }
        return total;
    }
}
