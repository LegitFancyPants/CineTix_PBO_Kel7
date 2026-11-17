package model;

import exception.TiketInvalidException;

/**
 * Kelas entitas yang merepresentasikan Transaksi pembelian tiket bioskop oleh pelanggan.
 * Menerapkan enkapsulasi, komposisi dengan kelas Tiket, serta polimorfisme Method Overloading
 * pada perhitungan total bayar (tanpa diskon vs dengan diskon member).
 */
public class Transaksi {
    private String idTransaksi;
    private String namaPembeli;
    private String tanggalTayang;
    private Tiket tiket;
    private int jumlahTiket;
    private boolean memberVip;
    private double totalBayar;

    public Transaksi(String idTransaksi, String namaPembeli, String tanggalTayang,
                     Tiket tiket, int jumlahTiket, boolean memberVip) throws TiketInvalidException {
        setIdTransaksi(idTransaksi);
        setNamaPembeli(namaPembeli);
        setTanggalTayang(tanggalTayang);
        setTiket(tiket);
        setJumlahTiket(jumlahTiket);
        this.memberVip = memberVip;
        perbaruiTotalBayar();
    }

    /**
     * Method Overloading 1: Menghitung total bayar normal tanpa potongan diskon.
     */
    public double hitungTotalBayar() {
        return tiket.hitungHarga() * jumlahTiket;
    }

    /**
     * Method Overloading 2: Menghitung total bayar setelah dikurangi persentase diskon member.
     *
     * @param persenDiskon Nilai diskon (misal 0.10 untuk 10%, atau 10.0 jika dalam skala 0-100)
     */
    public double hitungTotalBayar(double persenDiskon) {
        double diskonNormalisasi = persenDiskon;
        if (diskonNormalisasi > 1.0 && diskonNormalisasi <= 100.0) {
            diskonNormalisasi = diskonNormalisasi / 100.0;
        }
        if (diskonNormalisasi < 0.0) {
            diskonNormalisasi = 0.0;
        } else if (diskonNormalisasi > 1.0) {
            diskonNormalisasi = 1.0;
        }
        return (tiket.hitungHarga() * jumlahTiket) * (1.0 - diskonNormalisasi);
    }

    private void perbaruiTotalBayar() {
        if (this.tiket != null && this.jumlahTiket > 0) {
            this.totalBayar = this.memberVip ? hitungTotalBayar(0.10) : hitungTotalBayar();
        }
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) throws TiketInvalidException {
        if (idTransaksi == null || idTransaksi.trim().isEmpty()) {
            throw new TiketInvalidException("ID Transaksi tidak boleh kosong!");
        }
        this.idTransaksi = idTransaksi.trim();
    }

    public String getNamaPembeli() {
        return namaPembeli;
    }

    public void setNamaPembeli(String nama) throws TiketInvalidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new TiketInvalidException("Nama pembeli tidak boleh kosong!");
        }
        this.namaPembeli = nama.trim();
    }

    public String getTanggalTayang() {
        return tanggalTayang;
    }

    public void setTanggalTayang(String tanggalTayang) throws TiketInvalidException {
        if (tanggalTayang == null || tanggalTayang.trim().isEmpty()) {
            throw new TiketInvalidException("Tanggal tayang tidak boleh kosong!");
        }
        this.tanggalTayang = tanggalTayang.trim();
    }

    public Tiket getTiket() {
        return tiket;
    }

    public void setTiket(Tiket tiket) throws TiketInvalidException {
        if (tiket == null) {
            throw new TiketInvalidException("Data tiket pada transaksi tidak boleh kosong!");
        }
        this.tiket = tiket;
        perbaruiTotalBayar();
    }

    public int getJumlahTiket() {
        return jumlahTiket;
    }

    public void setJumlahTiket(int jumlah) throws TiketInvalidException {
        if (jumlah <= 0) {
            throw new TiketInvalidException("Jumlah pembelian tiket harus lebih dari 0!");
        }
        this.jumlahTiket = jumlah;
        perbaruiTotalBayar();
    }

    public boolean isMemberVip() {
        return memberVip;
    }

    public void setMemberVip(boolean memberVip) {
        this.memberVip = memberVip;
        perbaruiTotalBayar();
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(double totalBayar) throws TiketInvalidException {
        if (totalBayar < 0) {
            throw new TiketInvalidException("Total bayar tidak boleh bernilai negatif!");
        }
        this.totalBayar = totalBayar;
    }
}
