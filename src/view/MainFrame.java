package view;

import controller.BioskopController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Frame utama aplikasi CineTix (MainFrame) yang mengelola perpindahan antar 3 tampilan
 * (LoginPanel, FilmPanel, TransaksiPanel) menggunakan CardLayout dan JMenuBar konsisten,
 * serta menjaga sinkronisasi data antar-panel secara otomatis.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final String PANEL_LOGIN = "LOGIN";
    public static final String PANEL_FILM = "FILM";
    public static final String PANEL_TRANSAKSI = "TRANSAKSI";

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BioskopController controller;

    private LoginPanel loginPanel;
    private FilmPanel filmPanel;
    private TransaksiPanel transaksiPanel;

    private JButton btnNavLogin;
    private JButton btnNavFilm;
    private JButton btnNavTransaksi;

    private JMenuItem itemMenuFilm;
    private JMenuItem itemMenuTransaksi;
    private JMenuItem itemMenuLogout;

    /** Username petugas yang sedang login; null berarti belum login. */
    private String userAktif = null;

    public MainFrame() {
        this.controller = new BioskopController();
        initFrame();
        initMenuBar();
        initHeaderAndCards();
        perbaruiAksesNavigasi();
        tampilkanPanel(PANEL_LOGIN);
    }

    private void initFrame() {
        setTitle("CineTix - Sistem Informasi Penjualan Tiket Bioskop Terpadu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 740);
        setMinimumSize(new Dimension(920, 660));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNavigasi = new JMenu("Menu Navigasi");
        menuNavigasi.setFont(new Font("SansSerif", Font.BOLD, 13));

        JMenuItem itemLogin = new JMenuItem("1. Beranda / Login & Statistik");
        JMenuItem itemFilm = new JMenuItem("2. Kelola Data Film (Master)");
        JMenuItem itemTransaksi = new JMenuItem("3. Transaksi Penjualan Tiket");
        JMenuItem itemKeluar = new JMenuItem("Keluar Aplikasi");

        itemLogin.addActionListener(e -> tampilkanPanel(PANEL_LOGIN));
        itemFilm.addActionListener(e -> tampilkanPanel(PANEL_FILM));
        itemTransaksi.addActionListener(e -> tampilkanPanel(PANEL_TRANSAKSI));
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(e -> logout());
        itemMenuFilm = itemFilm;
        itemMenuTransaksi = itemTransaksi;
        itemMenuLogout = itemLogout;
        itemKeluar.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin keluar dari aplikasi CineTix?",
                    "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION
            );
            if (konfirmasi == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        menuNavigasi.add(itemLogin);
        menuNavigasi.add(itemFilm);
        menuNavigasi.add(itemTransaksi);
        menuNavigasi.addSeparator();
        menuNavigasi.add(itemLogout);
        menuNavigasi.add(itemKeluar);

        JMenu menuBantuan = new JMenu("Informasi & Tarif");
        menuBantuan.setFont(new Font("SansSerif", Font.BOLD, 13));

        JMenuItem itemRumus = new JMenuItem("Aturan Tarif Polimorfisme & Diskon");
        itemRumus.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                ATURAN PERHITUNGAN HARGA TIKET (POLIMORFISME OVERRIDING & OVERLOADING):

                1. Kelas Reguler (TiketReguler):
                   - Weekday : Harga Dasar Film
                   - Weekend : Harga Dasar Film + Rp 10.000

                2. Kelas VIP (TiketVIP - Kursi Recliner + Bonus Snack):
                   - Weekday : Harga Dasar Film x 1,5
                   - Weekend : (Harga Dasar Film x 1,5) + Rp 15.000

                3. Kelas Premiere (TiketPremiere - Lounge Eksklusif + Selimut):
                   - Weekday : Harga Dasar Film x 2,0
                   - Weekend : (Harga Dasar Film x 2,0) + Rp 25.000

                4. Status Member VIP (Method Overloading hitungTotalBayar):
                   - Non-Member : Harga Tiket x Jumlah Beli
                   - Member VIP : (Harga Tiket x Jumlah Beli) x 90% (Diskon 10%)
                """,
                "Informasi Rumus Tarif CineTix",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JMenuItem itemTentang = new JMenuItem("Tentang Aplikasi CineTix");
        itemTentang.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                CineTix — Sistem Informasi Penjualan Tiket Bioskop
                Arsitektur : MVC (Model-View-Controller) + DAO Pattern
                Database   : SQLite Relational Database (cinetix.db)
                Platform   : Java Swing Desktop Application (JDK 17+)
                """,
                "Tentang CineTix",
                JOptionPane.INFORMATION_MESSAGE
        ));

        menuBantuan.add(itemRumus);
        menuBantuan.add(itemTentang);

        menuBar.add(menuNavigasi);
        menuBar.add(menuBantuan);
        setJMenuBar(menuBar);
    }

    private void initHeaderAndCards() {
        // Bar Navigasi Visual di atas untuk memudahkan perpindahan antar-tampilan
        JPanel topNavPanel = new JPanel(new BorderLayout());
        topNavPanel.setBackground(new Color(30, 41, 59));
        topNavPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblBrand = new JLabel("CINETIX BIOSKOP POS");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBrand.setForeground(new Color(250, 204, 21));

        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navButtonsPanel.setOpaque(false);

        btnNavLogin = buatTombolNavigasi("Beranda / Login");
        btnNavFilm = buatTombolNavigasi("Kelola Data Film");
        btnNavTransaksi = buatTombolNavigasi("Transaksi Tiket");

        btnNavLogin.addActionListener(e -> tampilkanPanel(PANEL_LOGIN));
        btnNavFilm.addActionListener(e -> tampilkanPanel(PANEL_FILM));
        btnNavTransaksi.addActionListener(e -> tampilkanPanel(PANEL_TRANSAKSI));

        navButtonsPanel.add(btnNavLogin);
        navButtonsPanel.add(btnNavFilm);
        navButtonsPanel.add(btnNavTransaksi);

        topNavPanel.add(lblBrand, BorderLayout.WEST);
        topNavPanel.add(navButtonsPanel, BorderLayout.EAST);
        add(topNavPanel, BorderLayout.NORTH);

        // CardLayout Utama untuk 3 Panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this, controller);
        filmPanel = new FilmPanel(controller, this::sinkronkanSemuaPanel);
        transaksiPanel = new TransaksiPanel(controller, this::sinkronkanSemuaPanel);

        mainPanel.add(loginPanel, PANEL_LOGIN);
        mainPanel.add(filmPanel, PANEL_FILM);
        mainPanel.add(transaksiPanel, PANEL_TRANSAKSI);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton buatTombolNavigasi(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(165, 34));
        return btn;
    }

    /**
     * Menyinkronkan data di seluruh panel (statistik pada LoginPanel, daftar film pada
     * JComboBox TransaksiPanel, serta tabel transaksi dan film) setiap kali terjadi perubahan CRUD.
     */
    public void sinkronkanSemuaPanel() {
        if (loginPanel != null) {
            loginPanel.perbaruiStatistik();
        }
        if (transaksiPanel != null) {
            transaksiPanel.segarkanPilihanFilm();
            transaksiPanel.muatDataTabel();
        }
        if (filmPanel != null) {
            filmPanel.muatDataTabel();
        }
    }

    /**
     * Menampilkan panel berdasarkan konstanta namaPanel dan menyegarkan datanya.
     */
    public void tampilkanPanel(String namaPanel) {
        // Guard: selain halaman login, wajib sudah login terlebih dahulu.
        if (!PANEL_LOGIN.equals(namaPanel) && !sudahLogin()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Anda harus login terlebih dahulu untuk membuka halaman ini.",
                    "Akses Ditolak",
                    JOptionPane.WARNING_MESSAGE
            );
            namaPanel = PANEL_LOGIN;
        }
        sinkronkanSemuaPanel();
        cardLayout.show(mainPanel, namaPanel);
        perbaruiGayaTombolNav(namaPanel);
    }

    public boolean sudahLogin() {
        return userAktif != null;
    }

    /** Dipanggil LoginPanel setelah otentikasi berhasil. */
    public void setSesiLogin(String username) {
        this.userAktif = username;
        perbaruiAksesNavigasi();
    }

    public void logout() {
        this.userAktif = null;
        perbaruiAksesNavigasi();
        loginPanel.resetSesi();
        tampilkanPanel(PANEL_LOGIN);
    }

    private void perbaruiAksesNavigasi() {
        boolean login = sudahLogin();
        btnNavFilm.setEnabled(login);
        btnNavTransaksi.setEnabled(login);
        itemMenuFilm.setEnabled(login);
        itemMenuTransaksi.setEnabled(login);
        itemMenuLogout.setEnabled(login);
    }

    private void perbaruiGayaTombolNav(String panelAktif) {
        aturWarnaTombol(btnNavLogin, PANEL_LOGIN.equals(panelAktif));
        aturWarnaTombol(btnNavFilm, PANEL_FILM.equals(panelAktif));
        aturWarnaTombol(btnNavTransaksi, PANEL_TRANSAKSI.equals(panelAktif));
    }

    private void aturWarnaTombol(JButton btn, boolean aktif) {
        if (aktif) {
            btn.setBackground(new Color(37, 99, 235));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(147, 197, 253), 1));
        } else {
            btn.setBackground(new Color(51, 65, 85));
            btn.setForeground(new Color(226, 232, 240));
            btn.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
        }
    }
}
