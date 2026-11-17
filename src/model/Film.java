package model;

import exception.TiketInvalidException;

/**
 * Kelas entitas yang merepresentasikan data master Film.
 * Menerapkan enkapsulasi penuh dengan validasi pada setiap setter.
 */
public class Film {
    private String idFilm;
    private String judul;
    private String genre;
    private int durasiMenit;
    private String ratingUsia;
    private double hargaDasar;

    public Film(String idFilm, String judul, String genre, int durasiMenit, String ratingUsia, double hargaDasar)
            throws TiketInvalidException {
        setIdFilm(idFilm);
        setJudul(judul);
        setGenre(genre);
        setDurasiMenit(durasiMenit);
        setRatingUsia(ratingUsia);
        setHargaDasar(hargaDasar);
    }

    public String getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(String idFilm) throws TiketInvalidException {
        if (idFilm == null || idFilm.trim().isEmpty()) {
            throw new TiketInvalidException("ID Film tidak boleh kosong!");
        }
        this.idFilm = idFilm.trim();
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) throws TiketInvalidException {
        if (judul == null || judul.trim().isEmpty()) {
            throw new TiketInvalidException("Judul Film tidak boleh kosong!");
        }
        this.judul = judul.trim();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) throws TiketInvalidException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new TiketInvalidException("Genre Film tidak boleh kosong!");
        }
        this.genre = genre.trim();
    }

    public int getDurasiMenit() {
        return durasiMenit;
    }

    public void setDurasiMenit(int durasi) throws TiketInvalidException {
        if (durasi <= 0) {
            throw new TiketInvalidException("Durasi film harus lebih dari 0 menit!");
        }
        this.durasiMenit = durasi;
    }

    public String getRatingUsia() {
        return ratingUsia;
    }

    public void setRatingUsia(String rating) throws TiketInvalidException {
        if (rating == null || rating.trim().isEmpty()) {
            throw new TiketInvalidException("Rating usia film harus dipilih!");
        }
        this.ratingUsia = rating.trim();
    }

    public double getHargaDasar() {
        return hargaDasar;
    }

    public void setHargaDasar(double harga) throws TiketInvalidException {
        if (Double.isNaN(harga) || Double.isInfinite(harga) || harga < 0) {
            throw new TiketInvalidException("Harga dasar film tidak boleh bernilai negatif!");
        }
        this.hargaDasar = harga;
    }

    @Override
    public String toString() {
        return idFilm + " - " + judul;
    }
}
