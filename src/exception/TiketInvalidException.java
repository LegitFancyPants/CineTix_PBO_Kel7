package exception;

/**
 * Custom Exception untuk menangani kesalahan validasi aturan bisnis
 * pada entitas Film, Tiket, dan Transaksi di aplikasi CineTix.
 */
public class TiketInvalidException extends Exception {
    private static final long serialVersionUID = 1L;

    public TiketInvalidException(String pesan) {
        super(pesan);
    }

    public TiketInvalidException(String pesan, Throwable cause) {
        super(pesan, cause);
    }
}
