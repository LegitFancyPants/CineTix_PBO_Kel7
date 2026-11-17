package dao;

import java.util.List;

/**
 * Kontrak antarmuka generik (Generic Interface) untuk operasi CRUD basis data.
 * Diimplementasikan oleh kelas FilmDAO dan TransaksiDAO.
 *
 * @param <T> Tipe entitas model yang dikelola
 */
public interface DataRepository<T> {
    void simpan(T data);
    List<T> ambilSemua();
    void ubah(T data);
    void hapus(String id);
}
