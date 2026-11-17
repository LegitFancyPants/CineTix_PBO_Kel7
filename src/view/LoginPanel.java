package view;

import controller.BioskopController;
import exception.TiketInvalidException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Tampilan 1: Halaman Login & Dashboard Ringkasan Statistik (LoginPanel).
 * Seluruh validasi login dan pengambilan data statistik didelegasikan ke BioskopController.
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatistik;

    private JLabel lblAngkaFilm;
    private JLabel lblAngkaTransaksi;
    private JLabel lblAngkaTiket;
    private JLabel lblAngkaPendapatan;
    private JLabel lblStatusSesi;

    private final MainFrame frame;
    private final BioskopController controller;
    private final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public LoginPanel(MainFrame frame, BioskopController controller) {
        this.frame = frame;
        this.controller = controller;
        initComponents();
        perbaruiStatistik();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(20, 28, 20, 28));

        // 1. BAGIAN ATAS: Banner Sambutan
        JPanel bannerPanel = new JPanel(new BorderLayout(8, 6));
        bannerPanel.setBackground(new Color(15, 23, 42));
        bannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
                new EmptyBorder(18, 24, 18, 24)
        ));

        JLabel lblJudulBanner = new JLabel("SELAMAT DATANG DI CINETIX POS", SwingConstants.CENTER);
        lblJudulBanner.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblJudulBanner.setForeground(new Color(248, 250, 252));

        JLabel lblSubBanner = new JLabel(
                "Sistem Informasi Penjualan Tiket Bioskop Terpadu — Arsitektur MVC & SQLite JDBC",
                SwingConstants.CENTER);
        lblSubBanner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubBanner.setForeground(new Color(148, 163, 184));

        bannerPanel.add(lblJudulBanner, BorderLayout.CENTER);
        bannerPanel.add(lblSubBanner, BorderLayout.SOUTH);
        add(bannerPanel, BorderLayout.NORTH);

        // 2. BAGIAN TENGAH: Kartu Form Login Petugas
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(24, 36, 24, 36)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeaderLogin = new JLabel("Otentikasi Petugas / Kasir Bioskop", SwingConstants.CENTER);
        lblHeaderLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblHeaderLogin.setForeground(new Color(30, 41, 59));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginCard.add(lblHeaderLogin, gbc);

        lblStatusSesi = new JLabel("Status Sesi: Siap digunakan (Silakan login atau pilih menu di atas)", SwingConstants.CENTER);
        lblStatusSesi.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblStatusSesi.setForeground(new Color(71, 85, 105));
        gbc.gridy = 1;
        loginCard.add(lblStatusSesi, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        JLabel lblUser = new JLabel("Username :");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginCard.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtUsername = new JTextField("admin", 22);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(240, 32));
        loginCard.add(txtUsername, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        JLabel lblPass = new JLabel("Password :");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginCard.add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField("admin123", 22);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(240, 32));
        loginCard.add(txtPassword, gbc);

        // Info akun default untuk mempermudah pengujian
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel lblHint = new JLabel(
                "<html><div style='text-align:center;color:#0369a1;background:#e0f2fe;padding:6px 10px;border-radius:4px;'>"
                + "<b>Info Akun Demo:</b> Username: <code>admin</code> | Password: <code>admin123</code> "
                + "(atau <code>kasir</code> / <code>kasir123</code>)</div></html>",
                SwingConstants.CENTER);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginCard.add(lblHint, gbc);

        // Tombol Aksi Login & Reset
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        btnPanel.setOpaque(false);

        JButton btnLogin = new JButton("Masuk / Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(150, 36));

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnReset.setBackground(new Color(100, 116, 139));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReset.setPreferredSize(new Dimension(110, 36));

        JButton btnPintasanTransaksi = new JButton("Buka Kasir Tiket >>");
        btnPintasanTransaksi.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPintasanTransaksi.setBackground(new Color(5, 150, 105));
        btnPintasanTransaksi.setForeground(Color.WHITE);
        btnPintasanTransaksi.setFocusPainted(false);
        btnPintasanTransaksi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPintasanTransaksi.setPreferredSize(new Dimension(170, 36));

        btnPanel.add(btnLogin);
        btnPanel.add(btnReset);
        btnPanel.add(btnPintasanTransaksi);

        gbc.gridy = 5;
        loginCard.add(btnPanel, gbc);

        centerWrapper.add(loginCard);
        add(centerWrapper, BorderLayout.CENTER);

        // 3. BAGIAN BAWAH: Dashboard Ringkasan Statistik Real-Time
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setOpaque(false);

        lblStatistik = new JLabel("Ringkasan Statistik Bioskop CineTix (Real-Time SQLite)", SwingConstants.LEFT);
        lblStatistik.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblStatistik.setForeground(new Color(30, 41, 59));
        southPanel.add(lblStatistik, BorderLayout.NORTH);

        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        cardsGrid.setOpaque(false);

        lblAngkaFilm = new JLabel("0 Judul", SwingConstants.CENTER);
        lblAngkaTransaksi = new JLabel("0 Transaksi", SwingConstants.CENTER);
        lblAngkaTiket = new JLabel("0 Kursi", SwingConstants.CENTER);
        lblAngkaPendapatan = new JLabel("Rp 0", SwingConstants.CENTER);

        cardsGrid.add(buatKartuStatistik("Total Judul Film Aktif", lblAngkaFilm, new Color(37, 99, 235)));
        cardsGrid.add(buatKartuStatistik("Total Transaksi Tiket", lblAngkaTransaksi, new Color(16, 185, 129)));
        cardsGrid.add(buatKartuStatistik("Total Tiket Terjual", lblAngkaTiket, new Color(217, 119, 6)));
        cardsGrid.add(buatKartuStatistik("Total Pendapatan", lblAngkaPendapatan, new Color(124, 58, 237)));

        southPanel.add(cardsGrid, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Event Handling menggunakan Lambda Expression
        btnLogin.addActionListener(e -> prosesLogin());
        txtPassword.addActionListener(e -> prosesLogin());
        txtUsername.addActionListener(e -> prosesLogin());

        btnReset.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocusInWindow();
        });

        btnPintasanTransaksi.addActionListener(e -> {
            if (frame != null) {
                frame.tampilkanPanel(MainFrame.PANEL_TRANSAKSI);
            }
        });
    }

    private JPanel buatKartuStatistik(String judul, JLabel lblNilai, Color warnaAksen) {
        JPanel card = new JPanel(new BorderLayout(4, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, warnaAksen),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        new EmptyBorder(14, 14, 14, 14)
                )
        ));

        JLabel lblTitle = new JLabel(judul, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));

        lblNilai.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNilai.setForeground(new Color(15, 23, 42));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblNilai, BorderLayout.CENTER);
        return card;
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            boolean berhasil = controller.validasiLogin(username, password);
            if (berhasil) {
                lblStatusSesi.setText("Status Sesi: Login Aktif sebagai '" + username.trim() + "'");
                lblStatusSesi.setForeground(new Color(5, 150, 105));
                perbaruiStatistik();
                JOptionPane.showMessageDialog(
                        this,
                        "Selamat datang, " + username.trim().toUpperCase() + "!\n"
                        + "Otentikasi berhasil. Anda akan diarahkan ke halaman Kelola Data Film.",
                        "Login Berhasil",
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (frame != null) {
                    frame.tampilkanPanel(MainFrame.PANEL_FILM);
                }
            }
        } catch (TiketInvalidException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Gagal Login",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Memperbarui angka statistik pada kartu dashboard secara real-time dari BioskopController.
     */
    public final void perbaruiStatistik() {
        int totalFilm = controller.getTotalJudulFilmAktif();
        int totalTrx = controller.getTotalTransaksi();
        int totalTiket = controller.getTotalTiketTerjual();
        double totalPendapatan = controller.getTotalPendapatan();

        lblAngkaFilm.setText(totalFilm + " Judul");
        lblAngkaTransaksi.setText(totalTrx + " Transaksi");
        lblAngkaTiket.setText(totalTiket + " Kursi");
        lblAngkaPendapatan.setText(formatRupiah.format(totalPendapatan));

        lblStatistik.setText(String.format(
                "Ringkasan Statistik Bioskop CineTix — %d Film Aktif | %d Transaksi | Total %s",
                totalFilm, totalTrx, formatRupiah.format(totalPendapatan)
        ));
    }
}
