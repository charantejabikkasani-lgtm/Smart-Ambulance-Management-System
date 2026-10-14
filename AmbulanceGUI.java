
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.PrintWriter;
import java.net.Socket;

public class AmbulanceGUI extends JFrame {

    private final JTextField idField = new JTextField("AMB101");
    private final JComboBox<String> locationBox =
            new JComboBox<>(new String[]{
                    "Ambulance", "A", "B", "C"
            });
    private final JComboBox<String> emergencyBox =
            new JComboBox<>(new String[]{
                    "Cardiac Emergency",
                    "Road Accident",
                    "Breathing Difficulty",
                    "Other Emergency"
            });

    private final JLabel routeLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("● SYSTEM READY");
    private final JTextArea detailsArea = new JTextArea();
    private final RoadMap map = new RoadMap();

    public AmbulanceGUI() {
        setTitle("Smart Ambulance | Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 247, 252));

        add(header(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(mainPanel(), BorderLayout.CENTER);
        add(footer(), BorderLayout.SOUTH);

        calculateRoute();
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(12, 57, 100));
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel(
                "🚑  SMART AMBULANCE COMMUNICATION SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "FASTER ROUTES  |  SAFER RESPONSES");
        subtitle.setForeground(new Color(195, 224, 247));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    private JPanel sidebar() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(19, 47, 76));
        panel.setPreferredSize(new Dimension(175, 100));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 12, 20, 12));

        String[] items = {
                "▣  Dashboard",
                "⌖  Calculate Route",
                "✉  Send Alert",
                "◉  Live Tracking",
                "⚙  Traffic Control"
        };

        for (int i = 0; i < items.length; i++) {
            final int index = i;
            JButton button = new JButton(items[i]);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setMaximumSize(new Dimension(160, 44));
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(i == 0
                    ? new Color(20, 111, 188)
                    : new Color(19, 47, 76));
            button.setBorder(new EmptyBorder(10, 10, 10, 5));
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                if (index == 1) {
                    calculateRoute();
                } else if (index == 2) {
                    sendAlert();
                } else if (index == 3) {
                    new AmbulanceTracking().setVisible(true);
                } else if (index == 4) {
                    new TrafficControlGUI().setVisible(true);
                }
            });

            panel.add(button);
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    private JPanel mainPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel upper = new JPanel(new GridLayout(1, 2, 14, 0));
        upper.setOpaque(false);
        upper.add(emergencyCard());
        upper.add(mapCard());

        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0));
        lower.setOpaque(false);
        lower.add(routeCard());
        lower.add(alertCard());

        panel.add(upper, BorderLayout.CENTER);
        panel.add(lower, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel card(String title) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 224, 238)),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(new Color(20, 57, 91));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private JPanel emergencyCard() {
        JPanel panel = card("Emergency Details");
        JPanel form = new JPanel(new GridLayout(6, 1, 5, 5));
        form.setOpaque(false);

        form.add(new JLabel("Ambulance ID"));
        form.add(idField);
        form.add(new JLabel("Current location"));
        form.add(locationBox);
        form.add(new JLabel("Emergency type"));
        form.add(emergencyBox);

        panel.add(form, BorderLayout.CENTER);

        JButton routeButton = primaryButton("Calculate Fastest Route");
        routeButton.addActionListener(e -> calculateRoute());
        panel.add(routeButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel mapCard() {
        JPanel panel = card("Road Network");
        panel.add(map, BorderLayout.CENTER);
        return panel;
    }

    private JPanel routeCard() {
        JPanel panel = card("Route Information");

        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        routeLabel.setForeground(new Color(15, 132, 81));

        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        timeLabel.setForeground(new Color(15, 132, 81));

        JPanel content = new JPanel(new GridLayout(3, 1, 5, 5));
        content.setOpaque(false);
        content.add(new JLabel("FASTEST ROUTE"));
        content.add(routeLabel);
        content.add(timeLabel);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel alertCard() {
        JPanel panel = card("Hospital Communication");

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailsArea.setText("Ready to send emergency information.");

        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JButton sendButton = primaryButton("➤  Send Hospital Alert");
        sendButton.addActionListener(e -> sendAlert());
        panel.add(sendButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel footer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 16, 8, 16));

        statusLabel.setForeground(new Color(15, 132, 81));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel note = new JLabel("Demo mode | Simulated road network");
        note.setForeground(new Color(95, 111, 127));
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(note, BorderLayout.EAST);
        return panel;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(13, 111, 195));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 10, 11, 10));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        return button;
    }

    private String selectedLocation() {
        return (String) locationBox.getSelectedItem();
    }

    private void calculateRoute() {
        String start = selectedLocation();
        String result = NetworkModel.findFastestRoute(start, "Hospital");

        if (result.equals("No route found.")
                || result.equals("Unknown location.")) {
            routeLabel.setText(result);
            timeLabel.setText("--");
        } else {
            String[] parts = result.split(" \\| ");
            routeLabel.setText(parts[0]);
            timeLabel.setText(parts.length > 1
                    ? parts[1].replace("Estimated travel time: ", "")
                    : "");
        }

        map.setRoute(NetworkModel.getRouteNodes(start, "Hospital"));
        detailsArea.setText("Route calculated.\n" + result);
    }

    private void sendAlert() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an ambulance ID.",
                    "Missing information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String location = selectedLocation();
        String route = NetworkModel.findFastestRoute(location, "Hospital");

        String message = "Emergency Alert"
                + " | Ambulance ID: " + id
                + " | Current Location: " + location
                + " | Emergency Type: " + emergencyBox.getSelectedItem()
                + " | Fastest Route: " + route;

        detailsArea.setText(message);

        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter output = new PrintWriter(
                     socket.getOutputStream(), true)) {
            output.println(message);
            statusLabel.setText("● ALERT SENT SUCCESSFULLY");
            JOptionPane.showMessageDialog(this,
                    "Emergency alert sent to the hospital dashboard.",
                    "Alert sent",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setText("● HOSPITAL SERVER NOT CONNECTED");
            JOptionPane.showMessageDialog(this,
                    "Could not connect to port 5000.\n"
                    + "Start HospitalGUI first.\n\n"
                    + ex.getMessage(),
                    "Connection failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class RoadMap extends JPanel {
        private String[] route = {"Ambulance", "B", "Hospital"};

        RoadMap() {
            setPreferredSize(new Dimension(300, 270));
            setBackground(new Color(239, 247, 250));
        }

        void setRoute(String[] route) {
            this.route = route;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int[][] points = {
                    {35, 205}, {90, 75}, {145, 150},
                    {205, 65}, {265, 190}
            };
            int[][] edges = {
                    {0, 1}, {0, 2}, {1, 3}, {2, 3},
                    {3, 4}, {1, 4}, {2, 4}
            };

            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g.setColor(new Color(210, 222, 231));

            for (int[] edge : edges) {
                g.drawLine(points[edge[0]][0], points[edge[0]][1],
                        points[edge[1]][0], points[edge[1]][1]);
            }

            g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g.setColor(new Color(22, 174, 103));

            int[] indexes = new int[route.length];
            for (int i = 0; i < route.length; i++) {
                indexes[i] = nodeIndex(route[i]);
            }

            for (int i = 0; i < indexes.length - 1; i++) {
                if (indexes[i] >= 0 && indexes[i + 1] >= 0) {
                    int[] a = points[indexes[i]];
                    int[] b = points[indexes[i + 1]];
                    g.drawLine(a[0], a[1], b[0], b[1]);
                }
            }

            String[] names = {"Ambulance", "A", "B", "C", "Hospital"};
            for (int i = 0; i < points.length; i++) {
                int x = points[i][0], y = points[i][1];
                g.setColor(i == 4 ? new Color(225, 53, 66)
                        : new Color(15, 116, 207));
                g.fillOval(x - 13, y - 13, 26, 26);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                String label = i == 0 ? "AMB" : i == 4 ? "H" : names[i];
                g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2,
                        y + 4);
                g.setColor(new Color(32, 55, 76));
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.drawString(names[i], x - 18, y + 27);
            }

            g.dispose();
        }

        private int nodeIndex(String node) {
            switch (node) {
                case "Ambulance": return 0;
                case "A": return 1;
                case "B": return 2;
                case "C": return 3;
                case "Hospital": return 4;
                default: return -1;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AmbulanceGUI().setVisible(true));
    }
}
