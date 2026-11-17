package main;

import dao.DatabaseConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import view.MainFrame;

/**
 * Titik masuk utama (Entry Point) aplikasi desktop CineTix.
 * Mengatur Look and Feel (Nimbus), menginisialisasi skema & auto-seeding SQLite,
 * lalu meluncurkan MainFrame pada Event Dispatch Thread (EDT).
 */
public class MainApp {

    public static void main(String[] args) {
        // 1. Atur Look and Feel Nimbus agar tampilan GUI modern dan konsisten
        try {
            boolean nimbusDitemukan = false;
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusDitemukan = true;
                    break;
                }
            }
            if (!nimbusDitemukan) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
        }

        // 2. Inisialisasi tabel database SQLite & auto-seeding data awal
        DatabaseConnection.inisialisasiTabel();

        // 3. Jalankan GUI pada Event Dispatch Thread (EDT) Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
