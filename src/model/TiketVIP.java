package model;

import exception.TiketInvalidException;

/**
 * Kelas turunan Tiket untuk layanan studio VIP dengan kursi recliner dan bonus snack.
 * Rumus harga:
 * - Weekday: Harga Dasar * 1.5
 * - Weekend: (Harga Dasar * 1.5) + 15.000
 */
public class TiketVIP extends Tiket {
    private double biayaLayananRecliner = 15000.0;
    private boolean bonusSnack;

    public TiketVIP(String kodeTiket, Film film, String jenisHari, String nomorKursi, boolean bonusSnack)
            throws TiketInvalidException {
        super(kodeTiket, film, jenisHari, nomorKursi);
        this.bonusSnack = bonusSnack;
    }

    public TiketVIP(String kodeTiket, Film film, String jenisHari, String nomorKursi)
            throws TiketInvalidException {
        this(kodeTiket, film, jenisHari, nomorKursi, true);
    }

    @Override
    public double hitungHarga() {
        double hargaVip = film.getHargaDasar() * 1.5;
        if ("Weekend".equalsIgnoreCase(jenisHari)) {
            return hargaVip + biayaLayananRecliner;
        }
        return hargaVip;
    }

    @Override
    public String getJenisKelas() {
        return "VIP";
    }

    public boolean isBonusSnack() {
        return bonusSnack;
    }

    public void setBonusSnack(boolean bonusSnack) {
        this.bonusSnack = bonusSnack;
    }

    public double getBiayaLayananRecliner() {
        return biayaLayananRecliner;
    }

    public void setBiayaLayananRecliner(double biayaLayananRecliner) throws TiketInvalidException {
        if (biayaLayananRecliner < 0) {
            throw new TiketInvalidException("Biaya layanan recliner tidak boleh negatif!");
        }
        this.biayaLayananRecliner = biayaLayananRecliner;
    }
}
