import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TugasKuApp extends JFrame {   // Top-level container

    private final DefaultListModel<Tugas> model = new DefaultListModel<>();
    private JList<Tugas> daftar;
    private JTextField tfTugas;
    private JComboBox<Tugas.Prioritas> cbPrioritas;
    private JCheckBox chkKonfirmasi;
    private JLabel lblTotal, lblSelesai;
    private JProgressBar pbProgres;

    public TugasKuApp() {
        super("TugasKu - Daftar Tugas");
        setSize(520, 440);
        setMinimumSize(new Dimension(480, 360));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));                 // Layout 1: BorderLayout

        add(buatPanelInput(), BorderLayout.NORTH);
        add(buatPanelDaftar(), BorderLayout.CENTER);
        add(buatPanelInfo(), BorderLayout.EAST);
        add(buatPanelTombol(), BorderLayout.SOUTH);
    }

    // Intermediate container 1: input tugas (FlowLayout)
    private JPanel buatPanelInput() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));   // Layout 2: FlowLayout
        tfTugas = new JTextField(16);
        cbPrioritas = new JComboBox<>(Tugas.Prioritas.values());
        cbPrioritas.setSelectedItem(Tugas.Prioritas.SEDANG);
        JButton btnTambah = new JButton("Tambah");

        btnTambah.addActionListener(e -> tambahTugas());   // ActionListener

        // Event Adapter
        tfTugas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tambahTugas();
                }
            }
        });

        panel.add(new JLabel("Tugas:"));
        panel.add(tfTugas);
        panel.add(cbPrioritas);
        panel.add(btnTambah);
        return panel;
    }

    // Intermediate container 2: JScrollPane
    private JScrollPane buatPanelDaftar() {
        daftar = new JList<>(model);
        daftar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        daftar.setFixedCellHeight(28);

        // Renderer: warna berdasarkan prioritas, dicoret jika selesai
        daftar.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                Tugas t = (Tugas) value;
                String teks = escapeHtml(t.toString());
                lbl.setText("<html>" + (t.isSelesai() ? "<s>" + teks + "</s>" : teks) + "</html>");
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    lbl.setForeground(t.isSelesai() ? Color.GRAY : t.getPrioritas().getWarna());
                }
                return lbl;
            }
        });

        // Event Adapter: MouseAdapter
        daftar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    tandaiSelesai();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(daftar);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar (klik 2x untuk selesai)"));
        return scroll;
    }

    // Intermediate container 3: info + opsi (BoxLayout)
    private JPanel buatPanelInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));   // Layout 3: BoxLayout
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 12));

        lblTotal = new JLabel("Total: 0");
        lblSelesai = new JLabel("Selesai: 0");
        pbProgres = new JProgressBar(0, 100);
        pbProgres.setStringPainted(true);
        chkKonfirmasi = new JCheckBox("Konfirmasi hapus", true);

        panel.add(lblTotal);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblSelesai);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pbProgres);
        panel.add(Box.createVerticalStrut(16));
        panel.add(chkKonfirmasi);
        return panel;
    }

    // Intermediate container 4: tombol aksi (GridLayout)
    private JPanel buatPanelTombol() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));     // Layout 4: GridLayout
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JButton btnSelesai = new JButton("Tandai Selesai");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Hapus Semua");

        btnSelesai.addActionListener(e -> tandaiSelesai());
        btnHapus.addActionListener(e -> hapusTugas());
        btnBersih.addActionListener(e -> hapusSemua());

        panel.add(btnSelesai);
        panel.add(btnHapus);
        panel.add(btnBersih);
        return panel;
    }

    private void tambahTugas() {
        String teks = tfTugas.getText().trim();
        if (teks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tugas tidak boleh kosong!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        model.addElement(new Tugas(teks, (Tugas.Prioritas) cbPrioritas.getSelectedItem()));
        tfTugas.setText("");
        tfTugas.requestFocus();
        perbaruiInfo();
    }

    private void tandaiSelesai() {
        Tugas t = daftar.getSelectedValue();
        if (t == null) return;
        t.toggleSelesai();
        daftar.repaint();
        perbaruiInfo();
    }

    private void hapusTugas() {
        int i = daftar.getSelectedIndex();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu.");
            return;
        }
        if (chkKonfirmasi.isSelected() && !konfirmasi("Hapus tugas ini?")) return;
        model.remove(i);
        perbaruiInfo();
    }

    private void hapusSemua() {
        if (model.isEmpty()) return;
        if (chkKonfirmasi.isSelected() && !konfirmasi("Hapus semua tugas?")) return;
        model.clear();
        perbaruiInfo();
    }

    private boolean konfirmasi(String pesan) {
        return JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void perbaruiInfo() {
        int selesai = 0;
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).isSelesai()) selesai++;
        }
        int total = model.size();
        lblTotal.setText("Total: " + total);
        lblSelesai.setText("Selesai: " + selesai);
        pbProgres.setValue(total == 0 ? 0 : selesai * 100 / total);
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TugasKuApp().setVisible(true));
    }
}
