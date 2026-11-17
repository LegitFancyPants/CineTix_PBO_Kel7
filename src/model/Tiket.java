package model;

import exception.TiketInvalidException;

/**
 * Kelas induk abstrak (Abstract Class) untuk seluruh jenis tiket bioskop.
 * Menyimpan atribut dasar tiket dengan akses protected agar dapat diakses oleh subclass.
 */
public abstract class Tiket {
    protected String kodeTiket;
    protected Film film;
    protected String jenisHari;
    protected String nomorKursi;

    public Tiket(String kodeTiket, Film film, String jenisHari, String nomorKursi) throws TiketInvalidException {
        setKodeTiket(kodeTiket);
        setFilm(film);
        setJenisHari(jenisHari);
        setNomorKursi(nomorKursi);
    }

    /**
     * Menghitung harga per satu tiket berdasarkan kelas studio dan jenis hari.
     * Di-override secara polimorfik oleh masing-masing kelas turunan.
     */
    public abstract double hitungHarga();

    /**
     * Mengembalikan nama kelas layanan tiket ("Reguler", "VIP", atau "Premiere").
     */
    public abstract String getJenisKelas();

    public String getKodeTiket() {
        return kodeTiket;
    }

    public void setKodeTiket(String kode) throws TiketInvalidException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new TiketInvalidException("Kode tiket tidak boleh kosong!");
        }
        this.kodeTiket = kode.trim();
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) throws TiketInvalidException {
        if (film == null) {
            throw new TiketInvalidException("Objek Film pada tiket tidak boleh kosong (null)!");
        }
        this.film = film;
    }

    public String getJenisHari() {
        return jenisHari;
    }

    public void setJenisHari(String hari) throws TiketInvalidException {
        if (hari == null || hari.trim().isEmpty()) {
            throw new TiketInvalidException("Jenis hari tayang (Weekday/Weekend) harus dipilih!");
        }
        String trimmed = hari.trim();
        if (!trimmed.equalsIgnoreCase("Weekday") && !trimmed.equalsIgnoreCase("Weekend")) {
            throw new TiketInvalidException("Jenis hari harus 'Weekday' atau 'Weekend'!");
        }
        this.jenisHari = trimmed.equalsIgnoreCase("Weekend") ? "Weekend" : "Weekday";
    }

    public String getNomorKursi() {
        return nomorKursi;
    }

    public void setNomorKursi(String kursi) throws TiketInvalidException {
        if (kursi == null || kursi.trim().isEmpty()) {
            throw new TiketInvalidException("Nomor kursi tidak boleh kosong!");
        }
        this.nomorKursi = kursi.trim();
    }
}
