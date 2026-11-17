package model;

import exception.TiketInvalidException;

/**
 * Kelas turunan Tiket untuk layanan studio Reguler.
 * Rumus harga:
 * - Weekday: Harga Dasar
 * - Weekend: Harga Dasar + 10.000
 */
public class TiketReguler extends Tiket {
    private double biayaTambahanWeekend = 10000.0;

    public TiketReguler(String kodeTiket, Film film, String jenisHari, String nomorKursi)
            throws TiketInvalidException {
        super(kodeTiket, film, jenisHari, nomorKursi);
    }

    @Override
    public double hitungHarga() {
        double hargaDasar = film.getHargaDasar();
        if ("Weekend".equalsIgnoreCase(jenisHari)) {
            return hargaDasar + biayaTambahanWeekend;
        }
        return hargaDasar;
    }

    @Override
    public String getJenisKelas() {
        return "Reguler";
    }

    public double getBiayaTambahanWeekend() {
        return biayaTambahanWeekend;
    }

    public void setBiayaTambahanWeekend(double biayaTambahanWeekend) throws TiketInvalidException {
        if (biayaTambahanWeekend < 0) {
            throw new TiketInvalidException("Biaya tambahan weekend tidak boleh negatif!");
        }
        this.biayaTambahanWeekend = biayaTambahanWeekend;
    }
}
