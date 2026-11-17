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
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.Film;

/**
 * Tampilan 2: Manajemen Data Film (FilmPanel — CRUD Entitas 1).
 * Seluruh aksi tambah, ubah, hapus, dan pencarian didelegasikan ke BioskopController.
 */
public class FilmPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtIdFilm;
    private JTextField txtJudul;
    private JComboBox<String> cmbGenre;
    private JSpinner spnDurasi;
    private JRadioButton rbSU;
    private JRadioButton rb13;
    private JRadioButton rb17;
    private ButtonGroup bgRating;
    private JTextField txtHarga;
    private JTextField txtCari;
    private JTable tblFilm;
    private DefaultTableModel tableModel;

    private final BioskopController controller;
    private final Runnable onDataChangedCallback;
    private final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public FilmPanel(BioskopController controller) {
        this(controller, null);
    }

    public FilmPanel(BioskopController controller, Runnable onDataChangedCallback) {
        this.controller = controller;
        this.onDataChangedCallback = onDataChangedCallback;
        initComponents();
        muatDataTabel();
        resetForm();
    }

    private void initComponents() {
        setLayout(new BorderLayout(14, 14));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. PANEL ATAS: Form Input Data Film + Baris Tombol CRUD & Pencarian
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        TitledBorder titledForm = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " FORM INPUT DATA FILM (MASTER FILM) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(30, 41, 59)
        );
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledForm, new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kolom Kiri Baris 0: ID Film
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("ID Film :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        txtIdFilm = new JTextField(16);
        txtIdFilm.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(txtIdFilm, gbc);

        // Kolom Kanan Baris 0: Rating Usia (Radio Button)
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Rating Usia :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        ratingPanel.setOpaque(false);
        rbSU = new JRadioButton("SU (Semua Umur)");
        rb13 = new JRadioButton("13+ (Remaja)");
        rb17 = new JRadioButton("17+ (Dewasa)");
        rbSU.setOpaque(false);
        rb13.setOpaque(false);
        rb17.setOpaque(false);
        rbSU.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rb13.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rb17.setFont(new Font("SansSerif", Font.PLAIN, 13));

        bgRating = new ButtonGroup();
        bgRating.add(rbSU);
        bgRating.add(rb13);
        bgRating.add(rb17);
        rb13.setSelected(true);

        ratingPanel.add(rbSU);
        ratingPanel.add(rb13);
        ratingPanel.add(rb17);
        formPanel.add(ratingPanel, gbc);

        // Kolom Kiri Baris 1: Judul Film
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Judul Film :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        txtJudul = new JTextField(20);
        txtJudul.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(txtJudul, gbc);

        // Kolom Kanan Baris 1: Durasi (Menit) menggunakan JSpinner
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Durasi (Menit) :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        spnDurasi = new JSpinner(new SpinnerNumberModel(120, -500, 600, 5));
        spnDurasi.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(spnDurasi, gbc);

        // Kolom Kiri Baris 2: Genre (JComboBox)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Genre Film :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        cmbGenre = new JComboBox<>(new String[]{
            "Sci-Fi", "Action", "Drama", "Horror", "Comedy", "Animation", "Adventure", "Thriller"
        });
        cmbGenre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cmbGenre, gbc);

        // Kolom Kanan Baris 2: Harga Dasar (Rp)
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Harga Dasar (Rp) :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        txtHarga = new JTextField("45000", 16);
        txtHarga.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(txtHarga, gbc);

        topContainer.add(formPanel, BorderLayout.CENTER);

        // Baris Tombol Aksi CRUD + Kolom Pencarian
        JPanel actionBar = new JPanel(new BorderLayout(12, 8));
        actionBar.setBackground(Color.WHITE);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JPanel crudButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        crudButtons.setOpaque(false);

        JButton btnTambah = buatTombol("Tambah", new Color(37, 99, 235));
        JButton btnUbah = buatTombol("Ubah", new Color(217, 119, 6));
        JButton btnHapus = buatTombol("Hapus", new Color(220, 38, 38));
        JButton btnBersihkan = buatTombol("Bersihkan / Reset", new Color(100, 116, 139));

        crudButtons.add(btnTambah);
        crudButtons.add(btnUbah);
        crudButtons.add(btnHapus);
        crudButtons.add(btnBersihkan);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(buatLabel("Cari Film:"));
        txtCari = new JTextField(16);
        txtCari.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtCari.setToolTipText("Ketik judul, ID film, atau genre untuk mencari secara real-time");
        JButton btnCari = buatTombol("Go / Cari", new Color(15, 23, 42));
        JButton btnSemua = buatTombol("Semua", new Color(71, 85, 105));

        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnSemua);

        actionBar.add(crudButtons, BorderLayout.WEST);
        actionBar.add(searchPanel, BorderLayout.EAST);

        topContainer.add(actionBar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 2. PANEL TENGAH: Tabel Daftar Film (JTable)
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        TitledBorder titledTable = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " TABEL DAFTAR FILM BIOSKOP (KLIK BARIS UNTUK MEMILIH DATA) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(30, 41, 59)
        );
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledTable, new EmptyBorder(8, 12, 12, 12)));

        String[] kolom = {"ID Film", "Judul Film", "Genre", "Durasi (Menit)", "Rating Usia", "Harga Dasar (Rp)", "Harga Format"};
        tableModel = new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblFilm = new JTable(tableModel);
        tblFilm.setRowHeight(28);
        tblFilm.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblFilm.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblFilm.getTableHeader().setBackground(new Color(226, 232, 240));
        tblFilm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFilm.setFillsViewportHeight(true);

        // Lebar kolom proporsional
        tblFilm.getColumnModel().getColumn(0).setPreferredWidth(85);
        tblFilm.getColumnModel().getColumn(1).setPreferredWidth(240);
        tblFilm.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblFilm.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblFilm.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblFilm.getColumnModel().getColumn(5).setPreferredWidth(110);
        tblFilm.getColumnModel().getColumn(6).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tblFilm);
        scrollPane.setPreferredSize(new Dimension(800, 260));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        // 3. EVENT LISTENERS (Delegasi ke BioskopController)
        btnTambah.addActionListener(e -> aksiTambahFilm());
        btnUbah.addActionListener(e -> aksiUbahFilm());
        btnHapus.addActionListener(e -> aksiHapusFilm());
        btnBersihkan.addActionListener(e -> resetForm());

        btnCari.addActionListener(e -> aksiCariFilm());
        btnSemua.addActionListener(e -> {
            txtCari.setText("");
            muatDataTabel();
        });

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aksiCariFilm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aksiCariFilm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aksiCariFilm();
            }
        });

        tblFilm.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBarisTerpilih();
            }
        });
    }

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(new Color(30, 41, 59));
        return lbl;
    }

    private JButton buatTombol(String teks, Color warnaBg) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(warnaBg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 34));
        return btn;
    }

    private String getRatingTerpilih() {
        if (rbSU.isSelected()) {
            return "SU";
        } else if (rb17.isSelected()) {
            return "17+";
        }
        return "13+";
    }

    private void setRatingTerpilih(String rating) {
        if ("SU".equalsIgnoreCase(rating)) {
            rbSU.setSelected(true);
        } else if ("17+".equalsIgnoreCase(rating)) {
            rb17.setSelected(true);
        } else {
            rb13.setSelected(true);
        }
    }

    private void aksiTambahFilm() {
        try {
            String id = txtIdFilm.getText();
            String judul = txtJudul.getText();
            String genre = (String) cmbGenre.getSelectedItem();
            int durasi = (Integer) spnDurasi.getValue();
            String rating = getRatingTerpilih();
            double harga = parseHargaInput(txtHarga.getText());

            controller.tambahFilm(id, judul, genre, durasi, rating, harga);
            muatDataTabel();
            beritahuPerubahanData();
            resetForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Data film '" + judul.trim() + "' berhasil ditambahkan ke database SQLite!",
                    "Sukses Tambah Film",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Input Harga Dasar harus berupa angka yang valid (contoh: 45000)!",
                    "Kesalahan Format Angka",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (TiketInvalidException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validasi Film Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aksiUbahFilm() {
        try {
            String id = txtIdFilm.getText();
            String judul = txtJudul.getText();
            String genre = (String) cmbGenre.getSelectedItem();
            int durasi = (Integer) spnDurasi.getValue();
            String rating = getRatingTerpilih();
            double harga = parseHargaInput(txtHarga.getText());

            controller.ubahFilm(id, judul, genre, durasi, rating, harga);
            muatDataTabel();
            beritahuPerubahanData();
            resetForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Data film '" + id.trim() + "' berhasil diperbarui!",
                    "Sukses Ubah Film",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Input Harga Dasar harus berupa angka yang valid!",
                    "Kesalahan Format Angka",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (TiketInvalidException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validasi Ubah Film Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aksiHapusFilm() {
        String id = txtIdFilm.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih baris film pada tabel terlebih dahulu sebelum menghapus!",
                    "Peringatan Hapus",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus film dengan ID '" + id + "'?\n"
                + "(Perhatian: Transaksi yang terkait dengan film ini juga akan ikut terhapus).",
                "Konfirmasi Hapus Film",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                controller.hapusFilm(id);
                muatDataTabel();
                beritahuPerubahanData();
                resetForm();
                JOptionPane.showMessageDialog(
                        this,
                        "Film dengan ID '" + id + "' berhasil dihapus.",
                        "Sukses Hapus Film",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (TiketInvalidException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Gagal Menghapus Film",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private double parseHargaInput(String teksHarga) throws NumberFormatException {
        if (teksHarga == null || teksHarga.trim().isEmpty()) {
            throw new NumberFormatException("Harga kosong");
        }
        String bersih = teksHarga.trim().replace("Rp", "").replace(".", "").replace(",", ".").trim();
        // Jika pengguna memasukkan angka normal seperti 45000.0 atau -25000
        return Double.parseDouble(teksHarga.trim());
    }

    private void aksiCariFilm() {
        String keyword = txtCari.getText();
        List<Film> hasil = controller.cariFilm(keyword);
        tampilkanKeTabel(hasil);
    }

    /**
     * Memuat ulang seluruh data film dari BioskopController ke dalam JTable.
     */
    public final void muatDataTabel() {
        List<Film> daftar = controller.getDaftarFilm();
        tampilkanKeTabel(daftar);
    }

    private void tampilkanKeTabel(List<Film> daftar) {
        tableModel.setRowCount(0);
        for (Film f : daftar) {
            tableModel.addRow(new Object[]{
                f.getIdFilm(),
                f.getJudul(),
                f.getGenre(),
                f.getDurasiMenit(),
                f.getRatingUsia(),
                f.getHargaDasar(),
                formatRupiah.format(f.getHargaDasar())
            });
        }
    }

    private void isiFormDariBarisTerpilih() {
        int baris = tblFilm.getSelectedRow();
        if (baris >= 0) {
            txtIdFilm.setText(String.valueOf(tableModel.getValueAt(baris, 0)));
            txtJudul.setText(String.valueOf(tableModel.getValueAt(baris, 1)));
            cmbGenre.setSelectedItem(String.valueOf(tableModel.getValueAt(baris, 2)));
            spnDurasi.setValue(Integer.valueOf(String.valueOf(tableModel.getValueAt(baris, 3))));
            setRatingTerpilih(String.valueOf(tableModel.getValueAt(baris, 4)));

            double harga = Double.parseDouble(String.valueOf(tableModel.getValueAt(baris, 5)));
            if (harga == (long) harga) {
                txtHarga.setText(String.valueOf((long) harga));
            } else {
                txtHarga.setText(String.valueOf(harga));
            }
        }
    }

    public final void resetForm() {
        txtIdFilm.setText(controller.generateIdFilmOtomatis());
        txtJudul.setText("");
        cmbGenre.setSelectedIndex(0);
        spnDurasi.setValue(120);
        rb13.setSelected(true);
        txtHarga.setText("45000");
        tblFilm.clearSelection();
    }

    private void beritahuPerubahanData() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }
}
