import java.awt.Color;

public class Tugas {

    public enum Prioritas {
        TINGGI("Tinggi", new Color(198, 40, 40)),
        SEDANG("Sedang", new Color(230, 126, 0)),
        RENDAH("Rendah", new Color(46, 125, 50));

        private final String label;
        private final Color warna;

        Prioritas(String label, Color warna) {
            this.label = label;
            this.warna = warna;
        }

        public Color getWarna() {
            return warna;
        }

        @Override
        public String toString() {   // dipakai JComboBox
            return label;
        }
    }

    private final String nama;
    private final Prioritas prioritas;
    private boolean selesai;

    public Tugas(String nama, Prioritas prioritas) {
        this.nama = nama;
        this.prioritas = prioritas;
    }

    public String getNama() {
        return nama;
    }

    public Prioritas getPrioritas() {
        return prioritas;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void toggleSelesai() {
        selesai = !selesai;
    }

    @Override
    public String toString() {
        return nama + " (" + prioritas + ")";
    }
}
