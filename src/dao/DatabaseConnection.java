package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas pengelola koneksi tunggal (Singleton) ke basis data SQLite (cinetix.db),
 * pembuatan skema tabel otomatis (DDL), serta pengisian data awal (Auto-Seeding).
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:cinetix.db";
    private static Connection instance;

    private DatabaseConnection() {
        // Konstruktor privat untuk mencegah instansiasi langsung (Singleton Pattern)
    }

    /**
     * Mengembalikan objek Connection tunggal ke cinetix.db dan mengaktifkan PRAGMA foreign_keys.
     */
    public static synchronized Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                instance = DriverManager.getConnection(URL);
                try (Statement stmt = instance.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB Error] Driver SQLite JDBC tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB Error] Gagal membuka koneksi ke database SQLite: " + e.getMessage());
        }
        return instance;
    }

    /**
     * Membuat tabel film dan transaksi secara otomatis jika belum ada,
     * serta melakukan auto-seeding data contoh apabila tabel film masih kosong.
     */
    public static void inisialisasiTabel() {
        String sqlCreateFilm = """
                CREATE TABLE IF NOT EXISTS film (
                    id_film TEXT PRIMARY KEY,
                    judul TEXT NOT NULL,
                    genre TEXT NOT NULL,
                    durasi_menit INTEGER NOT NULL CHECK (durasi_menit > 0),
                    rating_usia TEXT NOT NULL,
                    harga_dasar REAL NOT NULL CHECK (harga_dasar >= 0)
                );
                """;

        String sqlCreateTransaksi = """
                CREATE TABLE IF NOT EXISTS transaksi (
                    id_transaksi TEXT PRIMARY KEY,
                    nama_pembeli TEXT NOT NULL,
                    tanggal_tayang TEXT NOT NULL,
                    id_film TEXT NOT NULL,
                    kelas_tiket TEXT NOT NULL,
                    jenis_hari TEXT NOT NULL,
                    nomor_kursi TEXT NOT NULL,
                    jumlah_tiket INTEGER NOT NULL CHECK (jumlah_tiket > 0),
                    member_vip INTEGER NOT NULL,
                    total_bayar REAL NOT NULL,
                    FOREIGN KEY (id_film) REFERENCES film(id_film) ON DELETE CASCADE
                );
                """;

        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.executeUpdate(sqlCreateFilm);
            stmt.executeUpdate(sqlCreateTransaksi);
            seedDataAwalJikaKosong(conn);
        } catch (SQLException e) {
            System.err.println("[DB Error] Gagal menginisialisasi tabel database: " + e.getMessage());
        }
    }

    /**
     * Mengisi 4 data film contoh dan 2 data transaksi contoh secara otomatis
     * apabila tabel film masih kosong saat aplikasi pertama kali dijalankan.
     */
    private static void seedDataAwalJikaKosong(Connection conn) {
        String sqlCek = "SELECT COUNT(*) AS jumlah FROM film";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCek)) {

            if (rs.next() && rs.getInt("jumlah") == 0) {
                String sqlInsertFilm = """
                        INSERT INTO film (id_film, judul, genre, durasi_menit, rating_usia, harga_dasar)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;

                Object[][] daftarFilmAwal = {
                    {"FLM-01", "Interstellar", "Sci-Fi", 169, "13+", 45000.0},
                    {"FLM-02", "The Conjuring", "Horror", 112, "17+", 40000.0},
                    {"FLM-03", "Spider-Man: Across the Spider-Verse", "Action", 140, "SU", 45000.0},
                    {"FLM-04", "Agak Laen", "Comedy", 119, "13+", 35000.0}
                };

                try (PreparedStatement psFilm = conn.prepareStatement(sqlInsertFilm)) {
                    for (Object[] f : daftarFilmAwal) {
                        psFilm.setString(1, (String) f[0]);
                        psFilm.setString(2, (String) f[1]);
                        psFilm.setString(3, (String) f[2]);
                        psFilm.setInt(4, (Integer) f[3]);
                        psFilm.setString(5, (String) f[4]);
                        psFilm.setDouble(6, (Double) f[5]);
                        psFilm.executeUpdate();
                    }
                }

                String sqlInsertTrx = """
                        INSERT INTO transaksi (
                            id_transaksi, nama_pembeli, tanggal_tayang, id_film,
                            kelas_tiket, jenis_hari, nomor_kursi, jumlah_tiket, member_vip, total_bayar
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                // Transaksi 1: FLM-01 (45.000), VIP, Weekend -> (45.000 * 1.5) + 15.000 = 82.500/tiket
                // Jumlah 2 tiket, Member VIP (Diskon 10%) -> (82.500 * 2) * 0.90 = 148.500
                // Transaksi 2: FLM-02 (40.000), Reguler, Weekday -> 40.000/tiket
                // Jumlah 2 tiket, Non-Member -> 40.000 * 2 = 80.000
                Object[][] daftarTrxAwal = {
                    {"TRX-001", "Budi Santoso", "17/11/2026", "FLM-01", "VIP", "Weekend", "A4, A5", 2, 1, 148500.0},
                    {"TRX-002", "Siti Aminah", "18/11/2026", "FLM-02", "Reguler", "Weekday", "C1, C2", 2, 0, 80000.0}
                };

                try (PreparedStatement psTrx = conn.prepareStatement(sqlInsertTrx)) {
                    for (Object[] t : daftarTrxAwal) {
                        psTrx.setString(1, (String) t[0]);
                        psTrx.setString(2, (String) t[1]);
                        psTrx.setString(3, (String) t[2]);
                        psTrx.setString(4, (String) t[3]);
                        psTrx.setString(5, (String) t[4]);
                        psTrx.setString(6, (String) t[5]);
                        psTrx.setString(7, (String) t[6]);
                        psTrx.setInt(8, (Integer) t[7]);
                        psTrx.setInt(9, (Integer) t[8]);
                        psTrx.setDouble(10, (Double) t[9]);
                        psTrx.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] Gagal melakukan auto-seeding data awal: " + e.getMessage());
        }
    }
}
