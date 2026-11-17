# CineTix — Sistem Informasi Penjualan Tiket Bioskop Terpadu

Aplikasi desktop berbasis **Java Swing (GUI)** dengan arsitektur **MVC (*Model-View-Controller*) + DAO (*Data Access Object*)** dan penyimpanan relasional lokal **SQLite (`cinetix.db`)** melalui JDBC.

---

## 1. Struktur Proyek & Kompatibilitas Eclipse IDE

Proyek ini telah dikonfigurasi lengkap dengan berkas `.project`, `.classpath`, dan pustaka eksternal di dalam folder `lib/` sehingga siap diimpor langsung ke **Eclipse IDE** tanpa konfigurasi tambahan.

```text
CineTix/
├── .classpath                              # Konfigurasi Build Path Eclipse (src, bin, lib/*.jar)
├── .project                                # Konfigurasi Proyek Java Eclipse
├── cinetix.db                              # Basis data SQLite (otomatis dibuat & di-seed)
├── lib/
│   ├── sqlite-jdbc-3.45.1.0.jar            # Driver JDBC SQLite
│   ├── slf4j-api-1.7.36.jar                # API Logging untuk SQLite JDBC
│   └── slf4j-nop-1.7.36.jar                # Implementasi No-Op Logger SLF4J
└── src/
    ├── main/
    │   └── MainApp.java                    # Titik masuk utama aplikasi (method main)
    ├── exception/
    │   └── TiketInvalidException.java      # Custom Exception untuk validasi aturan bisnis
    ├── model/
    │   ├── Film.java                       # Entitas CRUD 1 (Master Film)
    │   ├── Tiket.java                      # Abstract Class induk seluruh jenis tiket
    │   ├── TiketReguler.java               # Subclass 1 (Layanan Studio Reguler)
    │   ├── TiketVIP.java                   # Subclass 2 (Kursi Recliner + Bonus Snack)
    │   ├── TiketPremiere.java              # Subclass 3 (Lounge Eksklusif + Selimut)
    │   └── Transaksi.java                  # Entitas CRUD 2 (Transaksi Pembelian Tiket)
    ├── dao/
    │   ├── DataRepository.java             # Generic Interface CRUD (DataRepository<T>)
    │   ├── DatabaseConnection.java         # Singleton Koneksi SQLite, DDL, & Auto-Seeding
    │   ├── FilmDAO.java                    # Implementasi CRUD tabel film
    │   └── TransaksiDAO.java               # Implementasi CRUD tabel transaksi
    ├── controller/
    │   └── BioskopController.java          # Logika bisnis, cache Map<String, Film>, & penghubung MVC
    └── view/
        ├── MainFrame.java                  # JFrame utama dengan CardLayout & JMenuBar
        ├── LoginPanel.java                 # Tampilan 1: Login & Dashboard Statistik Real-Time
        ├── FilmPanel.java                  # Tampilan 2: CRUD & Pencarian Data Film
        └── TransaksiPanel.java             # Tampilan 3: CRUD Transaksi & Estimasi Harga Otomatis
```

---

## 2. Panduan Membuka & Menjalankan Proyek di Eclipse IDE

1. Buka **Eclipse IDE for Java Developers**.
2. Pilih menu **File** $\rightarrow$ **Open Projects from File System...** (atau **File** $\rightarrow$ **Import...** $\rightarrow$ **General** $\rightarrow$ **Existing Projects into Workspace**).
3. Klik **Directory...**, arahkan ke folder proyek **`CineTix`**, lalu klik **Finish**.
4. Di panel *Package Explorer*, buka `src/main/MainApp.java`.
5. Klik kanan pada `MainApp.java` $\rightarrow$ **Run As** $\rightarrow$ **Java Application**.

### Menjalankan melalui Terminal / PowerShell (Tanpa IDE)
```powershell
# 1. Kompilasi seluruh kode sumber ke folder bin/
javac -encoding UTF-8 -cp "lib/*" -d bin src/exception/*.java src/model/*.java src/dao/*.java src/controller/*.java src/view/*.java src/main/*.java

# 2. Jalankan aplikasi
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" main.MainApp
```

---

## 3. Informasi Akun Login & Auto-Seeding

* **Akun Demo Petugas / Kasir**:
  * **Username**: `admin` | **Password**: `admin123`
  * **Alternatif**: `kasir` | **Password**: `kasir123`
* **Data Awal Otomatis (*Auto-Seeding*)**:
  Saat aplikasi pertama kali dijalankan dan tabel `film` masih kosong, `DatabaseConnection.inisialisasiTabel()` otomatis mengisi:
  * **4 Data Film Contoh**:
    1. `FLM-01` — *Interstellar* (`Sci-Fi`, 169 menit, `13+`, Rp 45.000)
    2. `FLM-02` — *The Conjuring* (`Horror`, 112 menit, `17+`, Rp 40.000)
    3. `FLM-03` — *Spider-Man: Across the Spider-Verse* (`Action`, 140 menit, `SU`, Rp 45.000)
    4. `FLM-04` — *Agak Laen* (`Comedy`, 119 menit, `13+`, Rp 35.000)
  * **2 Data Transaksi Contoh**:
    1. `TRX-001` — *Budi Santoso* (`FLM-01`, `VIP`, `Weekend`, Kursi `A4, A5`, 2 tiket, Member VIP Diskon 10%, Total Rp 148.500)
    2. `TRX-002` — *Siti Aminah* (`FLM-02`, `Reguler`, `Weekday`, Kursi `C1, C2`, 2 tiket, Non-Member, Total Rp 80.000)

---

## 4. Pemenuhan Kriteria Konsep PBO (OOP) & MVC

1. **Enkapsulasi**:
   * Seluruh atribut pada `Film`, `Transaksi`, `TiketReguler`, `TiketVIP`, dan `TiketPremiere` bersifat `private`, sedangkan atribut induk pada `Tiket` bersifat `protected`.
   * Seluruh method `setter` memvalidasi nilai input dan melempar `TiketInvalidException` apabila tidak memenuhi aturan bisnis (misal: harga negatif, durasi $\le 0$, jumlah tiket $\le 0$, atau teks kosong).
2. **Pewarisan (*Inheritance*)**:
   * Kelas induk abstrak `Tiket` diturunkan ke 3 *subclass*: `TiketReguler`, `TiketVIP`, dan `TiketPremiere`, di mana setiap konstruktor *subclass* memanggil `super(kodeTiket, film, jenisHari, nomorKursi)`.
3. **Abstraksi (*Abstraction*)**:
   * *Abstract Class*: `Tiket` memiliki method abstrak `hitungHarga()` dan `getJenisKelas()`.
   * *Generic Interface*: `DataRepository<T>` diimplementasikan oleh 2 kelas DAO berbeda: `FilmDAO` (`DataRepository<Film>`) dan `TransaksiDAO` (`DataRepository<Transaksi>`).
4. **Polimorfisme (*Polymorphism*)**:
   * **Method Overriding**: `hitungHarga()` di-*override* pada masing-masing *subclass* `Tiket`:
     * `TiketReguler`: Weekday = $\text{Harga Dasar}$; Weekend = $\text{Harga Dasar} + 10.000$
     * `TiketVIP`: Weekday = $\text{Harga Dasar} \times 1,5$; Weekend = $(\text{Harga Dasar} \times 1,5) + 15.000$
     * `TiketPremiere`: Weekday = $\text{Harga Dasar} \times 2,0$; Weekend = $(\text{Harga Dasar} \times 2,0) + 25.000$
   * **Method Overloading**: Pada kelas `Transaksi`, method `hitungTotalBayar()` (harga normal) di-*overload* dengan `hitungTotalBayar(double persenDiskon)` (harga setelah diskon member $10\%$).
5. **Collection & Generics**:
   * `List<Film>` dan `List<Transaksi>` (`ArrayList<>`) untuk menampung hasil query SQLite dan pemetaan ke `JTable`.
   * `Map<String, Film>` (`HashMap<>`) pada `BioskopController` sebagai *cache* pencarian cepat objek `Film` berdasarkan `idFilm`.

---

## 5. Skenario Demo & Pengujian Fungsional

### Skenario 1: Validasi Enkapsulasi & Custom Exception (`TiketInvalidException`)
1. Buka tab **Kelola Data Film**.
2. Kosongkan kolom **Judul Film** atau masukkan nilai negatif pada **Harga Dasar (Rp)** (misalnya `-25000`), lalu klik tombol **Tambah**.
3. **Hasil**: Setter model melempar `TiketInvalidException` dan aplikasi menampilkan dialog `JOptionPane` (`ERROR_MESSAGE`) tanpa mengalami *crash* atau menyimpan data tidak valid ke SQLite.

### Skenario 2: Polimorfisme Perhitungan Harga Tiket (*Overriding* & *Overloading*)
1. Buka tab **Transaksi Tiket**.
2. Pilih film **FLM-02 - The Conjuring** (Harga Dasar Rp 40.000), pilih Kelas Studio **VIP**, pilih Jenis Hari **Weekend**, atur Jumlah Beli menjadi **2**, dan centang **Member VIP (Diskon 10%)**.
3. **Hasil**: Pratinjau estimasi harga langsung menghitung tarif `TiketVIP` Weekend sebesar $(40.000 \times 1,5) + 15.000 = \text{Rp }75.000/\text{tiket}$, lalu melalui *overloaded method* `hitungTotalBayar(0.10)` menghasilkan total $(\text{Rp }75.000 \times 2) \times 90\% = \text{Rp }135.000,00$.

### Skenario 3: Operasi CRUD Lengkap, Sinkronisasi Antar-Panel, & Persistensi SQLite
1. Pada **Kelola Data Film**, tambahkan film baru `FLM-05` (*Dune: Part Two*, `Sci-Fi`, 166 menit, `13+`, Harga `50000`).
2. Pindah ke **Transaksi Tiket** $\rightarrow$ perhatikan `FLM-05 - Dune: Part Two` langsung muncul secara otomatis pada `JComboBox` **Pilih Film**.
3. Simpan transaksi baru menggunakan film tersebut, lalu pindah ke **Beranda / Login** $\rightarrow$ kartu statistik **Total Judul Film Aktif**, **Total Transaksi Tiket**, **Total Tiket Terjual**, dan **Total Pendapatan** langsung diperbarui secara otomatis.
4. Ubah atau hapus salah satu data transaksi (muncul dialog konfirmasi `Yes/No`), tutup aplikasi, dan jalankan kembali $\rightarrow$ seluruh perubahan tersimpan permanen di `cinetix.db`.
