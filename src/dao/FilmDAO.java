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

/**
 * Kelas Data Access Object (DAO) untuk mengelola operasi CRUD tabel film di SQLite.
 * Mengimplementasikan generic interface DataRepository<Film>.
 */
public class FilmDAO implements DataRepository<Film> {
    private Connection conn;

    public FilmDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void simpan(Film film) {
        String sql = "INSERT INTO film (id_film, judul, genre, durasi_menit, rating_usia, harga_dasar) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, film.getIdFilm());
            ps.setString(2, film.getJudul());
            ps.setString(3, film.getGenre());
            ps.setInt(4, film.getDurasiMenit());
            ps.setString(5, film.getRatingUsia());
            ps.setDouble(6, film.getHargaDasar());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan data film ke database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Film> ambilSemua() {
        List<Film> daftarFilm = new ArrayList<>();
        String sql = "SELECT id_film, judul, genre, durasi_menit, rating_usia, harga_dasar FROM film ORDER BY id_film ASC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    Film film = new Film(
                            rs.getString("id_film"),
                            rs.getString("judul"),
                            rs.getString("genre"),
                            rs.getInt("durasi_menit"),
                            rs.getString("rating_usia"),
                            rs.getDouble("harga_dasar")
                    );
                    daftarFilm.add(film);
                } catch (TiketInvalidException e) {
                    System.err.println("[FilmDAO] Data film di database tidak valid: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("[FilmDAO] Gagal mengambil daftar film: " + e.getMessage());
        }
        return daftarFilm;
    }

    @Override
    public void ubah(Film film) {
        String sql = "UPDATE film SET judul = ?, genre = ?, durasi_menit = ?, rating_usia = ?, harga_dasar = ? WHERE id_film = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, film.getJudul());
            ps.setString(2, film.getGenre());
            ps.setInt(3, film.getDurasiMenit());
            ps.setString(4, film.getRatingUsia());
            ps.setDouble(5, film.getHargaDasar());
            ps.setString(6, film.getIdFilm());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengubah data film di database: " + e.getMessage(), e);
        }
    }

    @Override
    public void hapus(String idFilm) {
        String sql = "DELETE FROM film WHERE id_film = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, idFilm);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menghapus data film dari database: " + e.getMessage(), e);
        }
    }

    /**
     * Mencari daftar film berdasarkan kata kunci pada judul, ID film, atau genre.
     */
    public List<Film> cariBerdasarkanJudul(String keyword) {
        List<Film> hasilPencarian = new ArrayList<>();
        String sql = """
                SELECT id_film, judul, genre, durasi_menit, rating_usia, harga_dasar
                FROM film
                WHERE LOWER(judul) LIKE ? OR LOWER(id_film) LIKE ? OR LOWER(genre) LIKE ?
                ORDER BY id_film ASC
                """;
        String pola = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pola);
            ps.setString(2, pola);
            ps.setString(3, pola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        Film film = new Film(
                                rs.getString("id_film"),
                                rs.getString("judul"),
                                rs.getString("genre"),
                                rs.getInt("durasi_menit"),
                                rs.getString("rating_usia"),
                                rs.getDouble("harga_dasar")
                        );
                        hasilPencarian.add(film);
                    } catch (TiketInvalidException e) {
                        System.err.println("[FilmDAO] Data film hasil pencarian tidak valid: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[FilmDAO] Gagal mencari data film: " + e.getMessage());
        }
        return hasilPencarian;
    }

    /**
     * Mengambil satu objek Film berdasarkan ID Film.
     */
    public Film cariBerdasarkanId(String idFilm) {
        String sql = "SELECT id_film, judul, genre, durasi_menit, rating_usia, harga_dasar FROM film WHERE id_film = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, idFilm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Film(
                            rs.getString("id_film"),
                            rs.getString("judul"),
                            rs.getString("genre"),
                            rs.getInt("durasi_menit"),
                            rs.getString("rating_usia"),
                            rs.getDouble("harga_dasar")
                    );
                }
            }
        } catch (SQLException | TiketInvalidException e) {
            System.err.println("[FilmDAO] Gagal mencari film berdasarkan ID (" + idFilm + "): " + e.getMessage());
        }
        return null;
    }

    public Connection getConn() {
        return conn;
    }
}
