
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class TrafficControlGUI extends JFrame {

    private final JComboBox<String> roadBox =
            new JComboBox<>(new String[]{
                    "Ambulance-A", "Ambulance-B", "A-C",
                    "B-C", "C-Hospital", "A-Hospital",
                    "B-Hospital"
            });

    private final JSpinner timeSpinner =
            new JSpinner(new SpinnerNumberModel(15, 1, 60, 1));

    private final JLabel routeLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JLabel trafficStatus =
            new JLabel("Traffic simulation ready");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Road", "Travel Time (min)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable roadTable = new JTable(model);
    private final JTextArea log = new JTextArea(5, 35);

    public TrafficControlGUI() {
        setTitle("Smart Ambulance | Traffic Control");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 247, 252));

        add(header(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(content(), BorderLayout.CENTER);

        refreshTable();
        refreshRoute();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(12, 57, 100));
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel(
                "🚦  TRAFFIC CONTROL & DYNAMIC ROUTING");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "SIMULATE CONGESTION  |  RECALCULATE ROUTES");
        subtitle.setForeground(new Color(195, 224, 247));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 4));
        labels.setOpaque(false);
        labels.add(title);
        labels.add(subtitle);
        p.add(labels, BorderLayout.CENTER);
        return p;
    }

    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(170, 100));
        p.setBackground(new Color(19, 47, 76));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 12, 20, 12));

        for (String text : new String[]{
                "▣  Traffic Dashboard",
                "↻  Update Traffic",
                "⌖  Route Comparison"}) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setBorder(new EmptyBorder(12, 5, 12, 5));
            p.add(label);
        }
        return p;
    }

    private JPanel content() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new GridLayout(1, 2, 12, 0));
        top.setOpaque(false);
        top.add(controlsCard());
        top.add(routeCard());

        JPanel middle = card("Current Road Network");
        roadTable.setRowHeight(28);
        roadTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        roadTable.getTableHeader().setBackground(
                new Color(223, 236, 247));
        middle.add(new JScrollPane(roadTable), BorderLayout.CENTER);

        JPanel bottom = card("Traffic Update Log");
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 12));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        bottom.add(new JScrollPane(log), BorderLayout.CENTER);

        p.add(top, BorderLayout.NORTH);
        p.add(middle, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel controlsCard() {
        JPanel p = card("Traffic Simulation");

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 10));
        form.setOpaque(false);
        form.add(new JLabel("Select road"));
        form.add(roadBox);
        form.add(new JLabel("Travel time (minutes)"));
        form.add(timeSpinner);

        JButton update = button("Update Traffic",
                new Color(235, 126, 20));
        JButton reset = button("Reset Network",
                new Color(13, 111, 195));

        form.add(update);
        form.add(reset);
        form.add(new JLabel("Status"));
        form.add(trafficStatus);

        update.addActionListener(e -> updateTraffic());
        reset.addActionListener(e -> resetNetwork());

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    private JPanel routeCard() {
        JPanel p = card("Fastest Route");

        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        routeLabel.setForeground(new Color(15, 132, 81));

        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timeLabel.setForeground(new Color(15, 132, 81));

        JPanel info = new JPanel(new GridLayout(3, 1, 8, 8));
        info.setOpaque(false);
        info.add(new JLabel("CURRENT OPTIMAL PATH"));
        info.add(routeLabel);
        info.add(timeLabel);
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    private JPanel card(String title) {
        JPanel p = new JPanel(new BorderLayout(8, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 224, 238)),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel heading = new JLabel(title);
        heading.setForeground(new Color(20, 57, 91));
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        p.add(heading, BorderLayout.NORTH);
        return p;
    }

    private JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        return b;
    }

    private void updateTraffic() {
        String road = (String) roadBox.getSelectedItem();
        String[] parts = road.split("-", 2);
        int minutes = (Integer) timeSpinner.getValue();

        try {
            NetworkModel.updateRoad(parts[0], parts[1], minutes);

            trafficStatus.setText("Updated successfully");
            log.append("Updated " + road + " to "
                    + minutes + " minutes.\n");

            refreshTable();
            refreshRoute();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Traffic update failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);

        String[][] edges = {
                {"Ambulance", "A"},
                {"Ambulance", "B"},
                {"A", "C"},
                {"B", "C"},
                {"C", "Hospital"},
                {"A", "Hospital"},
                {"B", "Hospital"}
        };

        for (String[] edge : edges) {
            int minutes = NetworkModel.getRoadTime(edge[0], edge[1]);
            model.addRow(new Object[]{
                    edge[0] + " - " + edge[1],
                    minutes
            });
        }
    }

    private void refreshRoute() {
        String result = NetworkModel.findFastestRoute(
                "Ambulance", "Hospital");

        String[] parts = result.split(" \\| ");
        routeLabel.setText(parts[0]);
        timeLabel.setText(parts.length > 1
                ? parts[1].replace("Estimated travel time: ", "")
                : "");
    }

    private void resetNetwork() {
        NetworkModel.updateRoad("Ambulance", "A", 5);
        NetworkModel.updateRoad("Ambulance", "B", 3);
        NetworkModel.updateRoad("A", "C", 4);
        NetworkModel.updateRoad("B", "C", 2);
        NetworkModel.updateRoad("C", "Hospital", 5);
        NetworkModel.updateRoad("A", "Hospital", 12);
        NetworkModel.updateRoad("B", "Hospital", 10);

        // Restore the demonstration traffic condition.
        NetworkModel.updateRoad("B", "C", 15);

        trafficStatus.setText("Network reset");
        log.append("Network reset; B-C congestion restored to 15 min.\n");
        refreshTable();
        refreshRoute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TrafficControlGUI().setVisible(true));
    }
}
