package dao;

import exception.TiketInvalidException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Film;
import model.Tiket;
import model.TiketPremiere;
import model.TiketReguler;
import model.TiketVIP;
import model.Transaksi;

/**
 * Kelas Data Access Object (DAO) untuk mengelola operasi CRUD tabel transaksi di SQLite.
 * Mengimplementasikan generic interface DataRepository<Transaksi> serta menerapkan
 * instansiasi polimorfik saat membaca ResultSet.
 */
public class TransaksiDAO implements DataRepository<Transaksi> {
    private Connection conn;
    private FilmDAO filmDAO;

    public TransaksiDAO() {
        this.conn = DatabaseConnection.getConnection();
        this.filmDAO = new FilmDAO();
    }

    public TransaksiDAO(FilmDAO filmDAO) {
        this.conn = DatabaseConnection.getConnection();
        this.filmDAO = filmDAO;
    }

    @Override
    public void simpan(Transaksi trx) {
        String sql = """
                INSERT INTO transaksi (
                    id_transaksi, nama_pembeli, tanggal_tayang, id_film,
                    kelas_tiket, jenis_hari, nomor_kursi, jumlah_tiket, member_vip, total_bayar
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            Tiket tiket = trx.getTiket();
            ps.setString(1, trx.getIdTransaksi());
            ps.setString(2, trx.getNamaPembeli());
            ps.setString(3, trx.getTanggalTayang());
            ps.setString(4, tiket.getFilm().getIdFilm());
            ps.setString(5, tiket.getJenisKelas());
            ps.setString(6, tiket.getJenisHari());
            ps.setString(7, tiket.getNomorKursi());
            ps.setInt(8, trx.getJumlahTiket());
            ps.setInt(9, trx.isMemberVip() ? 1 : 0);
            ps.setDouble(10, trx.getTotalBayar());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan data transaksi ke database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaksi> ambilSemua() {
        List<Transaksi> daftarTransaksi = new ArrayList<>();
        String sql = """
                SELECT id_transaksi, nama_pembeli, tanggal_tayang, id_film,
                       kelas_tiket, jenis_hari, nomor_kursi, jumlah_tiket, member_vip, total_bayar
                FROM transaksi
                ORDER BY id_transaksi ASC
                """;
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi trx = petakanBarisKeTransaksi(rs);
                if (trx != null) {
                    daftarTransaksi.add(trx);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal mengambil daftar transaksi: " + e.getMessage());
        }
        return daftarTransaksi;
    }

    @Override
    public void ubah(Transaksi trx) {
        String sql = """
                UPDATE transaksi
                SET nama_pembeli = ?, tanggal_tayang = ?, id_film = ?,
                    kelas_tiket = ?, jenis_hari = ?, nomor_kursi = ?,
                    jumlah_tiket = ?, member_vip = ?, total_bayar = ?
                WHERE id_transaksi = ?
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            Tiket tiket = trx.getTiket();
            ps.setString(1, trx.getNamaPembeli());
            ps.setString(2, trx.getTanggalTayang());
            ps.setString(3, tiket.getFilm().getIdFilm());
            ps.setString(4, tiket.getJenisKelas());
            ps.setString(5, tiket.getJenisHari());
            ps.setString(6, tiket.getNomorKursi());
            ps.setInt(7, trx.getJumlahTiket());
            ps.setInt(8, trx.isMemberVip() ? 1 : 0);
            ps.setDouble(9, trx.getTotalBayar());
            ps.setString(10, trx.getIdTransaksi());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengubah data transaksi di database: " + e.getMessage(), e);
        }
    }

    @Override
    public void hapus(String idTransaksi) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, idTransaksi);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menghapus data transaksi dari database: " + e.getMessage(), e);
        }
    }

    /**
     * Mencari daftar transaksi berdasarkan kata kunci nama pembeli, ID transaksi, atau nomor kursi.
     */
    public List<Transaksi> cariBerdasarkanPembeli(String keyword) {
        List<Transaksi> hasilPencarian = new ArrayList<>();
        String sql = """
                SELECT t.id_transaksi, t.nama_pembeli, t.tanggal_tayang, t.id_film,
                       t.kelas_tiket, t.jenis_hari, t.nomor_kursi, t.jumlah_tiket, t.member_vip, t.total_bayar
                FROM transaksi t
                LEFT JOIN film f ON t.id_film = f.id_film
                WHERE LOWER(t.nama_pembeli) LIKE ?
                   OR LOWER(t.id_transaksi) LIKE ?
                   OR LOWER(f.judul) LIKE ?
                   OR LOWER(t.kelas_tiket) LIKE ?
                ORDER BY t.id_transaksi ASC
                """;
        String pola = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pola);
            ps.setString(2, pola);
            ps.setString(3, pola);
            ps.setString(4, pola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaksi trx = petakanBarisKeTransaksi(rs);
                    if (trx != null) {
                        hasilPencarian.add(trx);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal mencari transaksi: " + e.getMessage());
        }
        return hasilPencarian;
    }

    /**
     * Memetakan baris ResultSet menjadi objek Transaksi dengan menerapkan
     * Dynamic Binding / Polimorfisme pada referensi kelas induk Tiket.
     */
    private Transaksi petakanBarisKeTransaksi(ResultSet rs) throws SQLException {
        String idTransaksi = rs.getString("id_transaksi");
        String namaPembeli = rs.getString("nama_pembeli");
        String tanggalTayang = rs.getString("tanggal_tayang");
        String idFilm = rs.getString("id_film");
        String kelasTiket = rs.getString("kelas_tiket");
        String jenisHari = rs.getString("jenis_hari");
        String nomorKursi = rs.getString("nomor_kursi");
        int jumlahTiket = rs.getInt("jumlah_tiket");
        boolean memberVip = rs.getInt("member_vip") == 1;

        Film film = filmDAO.cariBerdasarkanId(idFilm);
        if (film == null) {
            return null;
        }

        try {
            String kodeTiket = "TKT-" + idTransaksi;
            Tiket tiket;
            if ("VIP".equalsIgnoreCase(kelasTiket)) {
                tiket = new TiketVIP(kodeTiket, film, jenisHari, nomorKursi, true);
            } else if ("Premiere".equalsIgnoreCase(kelasTiket)) {
                tiket = new TiketPremiere(kodeTiket, film, jenisHari, nomorKursi, true);
            } else {
                tiket = new TiketReguler(kodeTiket, film, jenisHari, nomorKursi);
            }
            return new Transaksi(idTransaksi, namaPembeli, tanggalTayang, tiket, jumlahTiket, memberVip);
        } catch (TiketInvalidException e) {
            System.err.println("[TransaksiDAO] Data transaksi di database tidak valid: " + e.getMessage());
            return null;
        }
    }

    public Connection getConn() {
        return conn;
    }
}
