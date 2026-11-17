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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.Film;
import model.Tiket;
import model.Transaksi;

/**
 * Tampilan 3: Transaksi Penjualan Tiket Bioskop (TransaksiPanel — CRUD Entitas 2).
 * Seluruh kalkulasi harga otomatis dan operasi database didelegasikan ke BioskopController.
 */
public class TransaksiPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField txtIdTrx;
    private JTextField txtPembeli;
    private JComboBox<Film> cmbFilm;
    private JComboBox<String> cmbKelas;
    private JRadioButton rbWeekday;
    private JRadioButton rbWeekend;
    private ButtonGroup bgHari;
    private JSpinner spnTanggal;
    private JSpinner spnJumlah;
    private JCheckBox chkMember;
    private JCheckBox chkFasilitas;
    private JTextField txtKursi;
    private JLabel lblEstimasiHarga;
    private JLabel lblRincianTarif;
    private JTextField txtCari;
    private JTable tblTransaksi;
    private DefaultTableModel tableModel;

    private final BioskopController controller;
    private final Runnable onDataChangedCallback;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private boolean sedangMemuatCombo = false;

    public TransaksiPanel(BioskopController controller) {
        this(controller, null);
    }

    public TransaksiPanel(BioskopController controller, Runnable onDataChangedCallback) {
        this.controller = controller;
        this.onDataChangedCallback = onDataChangedCallback;
        initComponents();
        segarkanPilihanFilm();
        muatDataTabel();
        resetForm();
    }

    private void initComponents() {
        setLayout(new BorderLayout(14, 14));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. PANEL ATAS: Form Pemesanan Tiket + Baris Tombol CRUD & Pencarian
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        TitledBorder titledForm = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " FORM PEMESANAN TIKET BIOSKOP (KASIR POS) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(30, 41, 59)
        );
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledForm, new EmptyBorder(10, 16, 10, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Baris 0 Kiri: ID Transaksi
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("ID Transaksi :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        txtIdTrx = new JTextField(15);
        txtIdTrx.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(txtIdTrx, gbc);

        // Baris 0 Kanan: Kelas Studio (JComboBox)
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Kelas Studio :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        cmbKelas = new JComboBox<>(new String[]{"Reguler", "VIP", "Premiere"});
        cmbKelas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cmbKelas, gbc);

        // Baris 1 Kiri: Nama Pembeli
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Nama Pembeli :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        txtPembeli = new JTextField(18);
        txtPembeli.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(txtPembeli, gbc);

        // Baris 1 Kanan: Jenis Hari (JRadioButton)
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Jenis Hari :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        JPanel hariPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        hariPanel.setOpaque(false);
        rbWeekday = new JRadioButton("Weekday (Sen-Jum)");
        rbWeekend = new JRadioButton("Weekend (Sab-Min)");
        rbWeekday.setOpaque(false);
        rbWeekend.setOpaque(false);
        rbWeekday.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rbWeekend.setFont(new Font("SansSerif", Font.PLAIN, 13));

        bgHari = new ButtonGroup();
        bgHari.add(rbWeekday);
        bgHari.add(rbWeekend);
        rbWeekend.setSelected(true);

        hariPanel.add(rbWeekday);
        hariPanel.add(rbWeekend);
        formPanel.add(hariPanel, gbc);

        // Baris 2 Kiri: Pilih Film (JComboBox<Film>)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Pilih Film :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        cmbFilm = new JComboBox<>();
        cmbFilm.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cmbFilm, gbc);

        // Baris 2 Kanan: Nomor Kursi
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Nomor Kursi :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        txtKursi = new JTextField("A4, A5", 15);
        txtKursi.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtKursi.setToolTipText("Contoh: A4, A5 atau B1");
        formPanel.add(txtKursi, gbc);

        // Baris 3 Kiri: Tanggal Tayang (JSpinner Date)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Tgl Tayang :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spnTanggal = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnTanggal, "dd/MM/yyyy");
        spnTanggal.setEditor(dateEditor);
        spnTanggal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(spnTanggal, gbc);

        // Baris 3 Kanan: Jumlah Beli Tiket (JSpinner Number)
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Jumlah Beli :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        spnJumlah = new JSpinner(new SpinnerNumberModel(2, -10, 50, 1));
        spnJumlah.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(spnJumlah, gbc);

        // Baris 4 Kiri: Status Member & Fasilitas (JCheckBox)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Opsi & Member :"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkPanel.setOpaque(false);
        chkMember = new JCheckBox("Member VIP (Diskon 10%)");
        chkFasilitas = new JCheckBox("Bonus Snack / Selimut", true);
        chkMember.setOpaque(false);
        chkFasilitas.setOpaque(false);
        chkMember.setFont(new Font("SansSerif", Font.BOLD, 12));
        chkFasilitas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        checkPanel.add(chkMember);
        checkPanel.add(chkFasilitas);
        formPanel.add(checkPanel, gbc);

        // Baris 4 Kanan: Pratinjau Estimasi Total Bayar Real-time
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buatLabel("Estimasi Total :"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        JPanel estimasiBox = new JPanel(new BorderLayout(4, 2));
        estimasiBox.setBackground(new Color(236, 253, 245));
        estimasiBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        lblEstimasiHarga = new JLabel("Rp 0,00");
        lblEstimasiHarga.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEstimasiHarga.setForeground(new Color(4, 120, 87));

        lblRincianTarif = new JLabel("Harga/tiket: Rp 0");
        lblRincianTarif.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRincianTarif.setForeground(new Color(71, 85, 105));

        estimasiBox.add(lblEstimasiHarga, BorderLayout.CENTER);
        estimasiBox.add(lblRincianTarif, BorderLayout.SOUTH);
        formPanel.add(estimasiBox, gbc);

        topContainer.add(formPanel, BorderLayout.CENTER);

        // Baris Tombol CRUD & Pencarian Transaksi
        JPanel actionBar = new JPanel(new BorderLayout(12, 8));
        actionBar.setBackground(Color.WHITE);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JPanel crudButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        crudButtons.setOpaque(false);

        JButton btnSimpan = buatTombol("Simpan Transaksi", new Color(5, 150, 105));
        JButton btnUbah = buatTombol("Ubah", new Color(217, 119, 6));
        JButton btnHapus = buatTombol("Hapus", new Color(220, 38, 38));
        JButton btnReset = buatTombol("Reset", new Color(100, 116, 139));

        crudButtons.add(btnSimpan);
        crudButtons.add(btnUbah);
        crudButtons.add(btnHapus);
        crudButtons.add(btnReset);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(buatLabel("Cari Pembeli/Trx:"));
        txtCari = new JTextField(15);
        txtCari.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton btnCari = buatTombol("Go / Cari", new Color(15, 23, 42));
        JButton btnSemua = buatTombol("Semua", new Color(71, 85, 105));

        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnSemua);

        actionBar.add(crudButtons, BorderLayout.WEST);
        actionBar.add(searchPanel, BorderLayout.EAST);

        topContainer.add(actionBar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 2. PANEL TENGAH: Tabel Riwayat Transaksi Tiket (JTable)
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        TitledBorder titledTable = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " TABEL RIWAYAT TRANSAKSI TIKET (KLIK BARIS UNTUK MEMILIH DATA) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13),
                new Color(30, 41, 59)
        );
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledTable, new EmptyBorder(8, 12, 12, 12)));

        String[] kolom = {
            "ID Trx", "Nama Pembeli", "Tgl Tayang", "Judul Film",
            "Kelas", "Hari", "Jml & Kursi", "Member VIP", "Total Bayar"
        };
        tableModel = new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTransaksi = new JTable(tableModel);
        tblTransaksi.setRowHeight(28);
        tblTransaksi.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblTransaksi.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tblTransaksi.getTableHeader().setBackground(new Color(226, 232, 240));
        tblTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransaksi.setFillsViewportHeight(true);

        tblTransaksi.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTransaksi.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblTransaksi.getColumnModel().getColumn(2).setPreferredWidth(95);
        tblTransaksi.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblTransaksi.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblTransaksi.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblTransaksi.getColumnModel().getColumn(6).setPreferredWidth(115);
        tblTransaksi.getColumnModel().getColumn(7).setPreferredWidth(90);
        tblTransaksi.getColumnModel().getColumn(8).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tblTransaksi);
        scrollPane.setPreferredSize(new Dimension(800, 240));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        // 3. EVENT LISTENERS (Real-time Estimasi & CRUD via BioskopController)
        cmbFilm.addActionListener(e -> perbaruiEstimasiHarga());
        cmbKelas.addActionListener(e -> {
            String kelas = (String) cmbKelas.getSelectedItem();
            chkFasilitas.setEnabled(!"Reguler".equalsIgnoreCase(kelas));
            perbaruiEstimasiHarga();
        });
        rbWeekday.addActionListener(e -> perbaruiEstimasiHarga());
        rbWeekend.addActionListener(e -> perbaruiEstimasiHarga());
        spnJumlah.addChangeListener(e -> perbaruiEstimasiHarga());
        chkMember.addActionListener(e -> perbaruiEstimasiHarga());

        btnSimpan.addActionListener(e -> aksiSimpanTransaksi());
        btnUbah.addActionListener(e -> aksiUbahTransaksi());
        btnHapus.addActionListener(e -> aksiHapusTransaksi());
        btnReset.addActionListener(e -> resetForm());

        btnCari.addActionListener(e -> aksiCariTransaksi());
        btnSemua.addActionListener(e -> {
            txtCari.setText("");
            muatDataTabel();
        });

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aksiCariTransaksi();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aksiCariTransaksi();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aksiCariTransaksi();
            }
        });

        tblTransaksi.getSelectionModel().addListSelectionListener(e -> {
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

    private String getHariTerpilih() {
        return rbWeekend.isSelected() ? "Weekend" : "Weekday";
    }

    /**
     * Menghitung dan menampilkan pratinjau total bayar secara real-time
     * dengan mendelegasikan perhitungan polimorfik ke BioskopController.
     */
    private void perbaruiEstimasiHarga() {
        if (sedangMemuatCombo) {
            return;
        }
        Film film = (Film) cmbFilm.getSelectedItem();
        String kelas = (String) cmbKelas.getSelectedItem();
        String hari = getHariTerpilih();
        int jumlah = (Integer) spnJumlah.getValue();
        boolean member = chkMember.isSelected();

        double hargaSatuan = controller.hitungHargaSatuan(kelas, film, hari);
        double estimasiTotal = controller.hitungEstimasiTotal(kelas, film, hari, jumlah, member);

        lblEstimasiHarga.setText(formatRupiah.format(estimasiTotal));
        String infoDiskon = member ? " (Diskon Member 10%)" : " (Harga Normal)";
        lblRincianTarif.setText("Tarif " + kelas + " " + hari + ": " + formatRupiah.format(hargaSatuan) + "/tiket" + infoDiskon);
    }

    /**
     * Menyinkronkan isi JComboBox<Film> dengan daftar film terbaru dari BioskopController.
     */
    public final void segarkanPilihanFilm() {
        sedangMemuatCombo = true;
        Film filmSebelumnya = (Film) cmbFilm.getSelectedItem();
        String idSebelumnya = filmSebelumnya != null ? filmSebelumnya.getIdFilm() : null;

        cmbFilm.removeAllItems();
        List<Film> daftarFilm = controller.getDaftarFilm();
        Film itemTerpilih = null;
        for (Film f : daftarFilm) {
            cmbFilm.addItem(f);
            if (idSebelumnya != null && f.getIdFilm().equalsIgnoreCase(idSebelumnya)) {
                itemTerpilih = f;
            }
        }
        if (itemTerpilih != null) {
            cmbFilm.setSelectedItem(itemTerpilih);
        } else if (cmbFilm.getItemCount() > 0) {
            cmbFilm.setSelectedIndex(0);
        }
        sedangMemuatCombo = false;
        perbaruiEstimasiHarga();
    }

    private void aksiSimpanTransaksi() {
        try {
            String idTrx = txtIdTrx.getText();
            String pembeli = txtPembeli.getText();
            Film film = (Film) cmbFilm.getSelectedItem();
            if (film == null) {
                throw new TiketInvalidException("Pilih film terlebih dahulu! Pastikan data film tersedia.");
            }
            String kelas = (String) cmbKelas.getSelectedItem();
            String hari = getHariTerpilih();
            String kursi = txtKursi.getText();
            Date tglDate = (Date) spnTanggal.getValue();
            String tglTayang = dateFormat.format(tglDate);
            int jumlah = (Integer) spnJumlah.getValue();
            boolean member = chkMember.isSelected();
            boolean fasilitas = chkFasilitas.isSelected();

            String kodeTiket = "TKT-" + (idTrx == null ? "" : idTrx.trim());
            Tiket tiket = controller.buatTiket(kelas, kodeTiket, film, hari, kursi, fasilitas);
            controller.simpanTransaksi(idTrx, pembeli, tglTayang, tiket, jumlah, member);

            muatDataTabel();
            beritahuPerubahanData();
            resetForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi tiket untuk '" + pembeli.trim() + "' berhasil disimpan!",
                    "Sukses Simpan Transaksi",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (TiketInvalidException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validasi Transaksi Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aksiUbahTransaksi() {
        try {
            String idTrx = txtIdTrx.getText();
            String pembeli = txtPembeli.getText();
            Film film = (Film) cmbFilm.getSelectedItem();
            if (film == null) {
                throw new TiketInvalidException("Pilih film terlebih dahulu!");
            }
            String kelas = (String) cmbKelas.getSelectedItem();
            String hari = getHariTerpilih();
            String kursi = txtKursi.getText();
            Date tglDate = (Date) spnTanggal.getValue();
            String tglTayang = dateFormat.format(tglDate);
            int jumlah = (Integer) spnJumlah.getValue();
            boolean member = chkMember.isSelected();
            boolean fasilitas = chkFasilitas.isSelected();

            String kodeTiket = "TKT-" + (idTrx == null ? "" : idTrx.trim());
            Tiket tiket = controller.buatTiket(kelas, kodeTiket, film, hari, kursi, fasilitas);
            controller.ubahTransaksi(idTrx, pembeli, tglTayang, tiket, jumlah, member);

            muatDataTabel();
            beritahuPerubahanData();
            resetForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Data transaksi '" + idTrx.trim() + "' berhasil diperbarui!",
                    "Sukses Ubah Transaksi",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (TiketInvalidException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validasi Ubah Transaksi Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aksiHapusTransaksi() {
        String idTrx = txtIdTrx.getText().trim();
        if (idTrx.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih baris transaksi pada tabel terlebih dahulu sebelum menghapus!",
                    "Peringatan Hapus",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus transaksi '" + idTrx + "'?",
                "Konfirmasi Hapus Transaksi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                controller.hapusTransaksi(idTrx);
                muatDataTabel();
                beritahuPerubahanData();
                resetForm();
                JOptionPane.showMessageDialog(
                        this,
                        "Data transaksi '" + idTrx + "' berhasil dihapus.",
                        "Sukses Hapus Transaksi",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (TiketInvalidException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Gagal Menghapus Transaksi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void aksiCariTransaksi() {
        String keyword = txtCari.getText();
        List<Transaksi> hasil = controller.cariTransaksi(keyword);
        tampilkanKeTabel(hasil);
    }

    /**
     * Memuat ulang seluruh data transaksi dari BioskopController ke dalam JTable.
     */
    public final void muatDataTabel() {
        List<Transaksi> daftar = controller.getDaftarTransaksi();
        tampilkanKeTabel(daftar);
    }

    private void tampilkanKeTabel(List<Transaksi> daftar) {
        tableModel.setRowCount(0);
        for (Transaksi t : daftar) {
            Tiket tiket = t.getTiket();
            Film film = tiket.getFilm();
            String infoJmlKursi = t.getJumlahTiket() + " (" + tiket.getNomorKursi() + ")";
            tableModel.addRow(new Object[]{
                t.getIdTransaksi(),
                t.getNamaPembeli(),
                t.getTanggalTayang(),
                film.getIdFilm() + " - " + film.getJudul(),
                tiket.getJenisKelas(),
                tiket.getJenisHari(),
                infoJmlKursi,
                t.isMemberVip() ? "Ya (Diskon 10%)" : "Tidak",
                formatRupiah.format(t.getTotalBayar())
            });
        }
    }

    private void isiFormDariBarisTerpilih() {
        int baris = tblTransaksi.getSelectedRow();
        if (baris < 0) {
            return;
        }
        String idTrx = String.valueOf(tableModel.getValueAt(baris, 0));
        List<Transaksi> semua = controller.getDaftarTransaksi();
        for (Transaksi t : semua) {
            if (t.getIdTransaksi().equalsIgnoreCase(idTrx)) {
                txtIdTrx.setText(t.getIdTransaksi());
                txtPembeli.setText(t.getNamaPembeli());

                // Pilih film yang sesuai pada JComboBox
                String idFilm = t.getTiket().getFilm().getIdFilm();
                for (int i = 0; i < cmbFilm.getItemCount(); i++) {
                    Film item = cmbFilm.getItemAt(i);
                    if (item != null && item.getIdFilm().equalsIgnoreCase(idFilm)) {
                        cmbFilm.setSelectedIndex(i);
                        break;
                    }
                }

                cmbKelas.setSelectedItem(t.getTiket().getJenisKelas());
                if ("Weekend".equalsIgnoreCase(t.getTiket().getJenisHari())) {
                    rbWeekend.setSelected(true);
                } else {
                    rbWeekday.setSelected(true);
                }

                txtKursi.setText(t.getTiket().getNomorKursi());

                try {
                    Date parsedDate = dateFormat.parse(t.getTanggalTayang());
                    spnTanggal.setValue(parsedDate);
                } catch (ParseException ignored) {
                }

                spnJumlah.setValue(t.getJumlahTiket());
                chkMember.setSelected(t.isMemberVip());
                perbaruiEstimasiHarga();
                break;
            }
        }
    }

    public final void resetForm() {
        txtIdTrx.setText(controller.generateIdTransaksiOtomatis());
        txtPembeli.setText("");
        if (cmbFilm.getItemCount() > 0) {
            cmbFilm.setSelectedIndex(0);
        }
        cmbKelas.setSelectedItem("VIP");
        rbWeekend.setSelected(true);
        txtKursi.setText("A4, A5");
        spnTanggal.setValue(new Date());
        spnJumlah.setValue(2);
        chkMember.setSelected(true);
        chkFasilitas.setSelected(true);
        tblTransaksi.clearSelection();
        perbaruiEstimasiHarga();
    }

    private void beritahuPerubahanData() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }
}
