package model;

import exception.TiketInvalidException;

/**
 * Kelas turunan Tiket untuk layanan studio Premiere dengan fasilitas lounge eksklusif dan selimut.
 * Rumus harga:
 * - Weekday: Harga Dasar * 2.0
 * - Weekend: (Harga Dasar * 2.0) + 25.000
 */
public class TiketPremiere extends Tiket {
    private double biayaLounge = 25000.0;
    private boolean fasilitasSelimut;

    public TiketPremiere(String kodeTiket, Film film, String jenisHari, String nomorKursi, boolean fasilitasSelimut)
            throws TiketInvalidException {
        super(kodeTiket, film, jenisHari, nomorKursi);
        this.fasilitasSelimut = fasilitasSelimut;
    }

    public TiketPremiere(String kodeTiket, Film film, String jenisHari, String nomorKursi)
            throws TiketInvalidException {
        this(kodeTiket, film, jenisHari, nomorKursi, true);
    }

    @Override
    public double hitungHarga() {
        double hargaPremiere = film.getHargaDasar() * 2.0;
        if ("Weekend".equalsIgnoreCase(jenisHari)) {
            return hargaPremiere + biayaLounge;
        }
        return hargaPremiere;
    }

    @Override
    public String getJenisKelas() {
        return "Premiere";
    }

    public boolean isFasilitasSelimut() {
        return fasilitasSelimut;
    }

    public void setFasilitasSelimut(boolean fasilitasSelimut) {
        this.fasilitasSelimut = fasilitasSelimut;
    }

    public double getBiayaLounge() {
        return biayaLounge;
    }

    public void setBiayaLounge(double biayaLounge) throws TiketInvalidException {
        if (biayaLounge < 0) {
            throw new TiketInvalidException("Biaya fasilitas lounge tidak boleh negatif!");
        }
        this.biayaLounge = biayaLounge;
    }
}
