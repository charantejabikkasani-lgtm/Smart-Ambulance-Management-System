
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HospitalGUI extends JFrame {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Time", "Ambulance ID", "Location",
                    "Emergency", "Route / Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JLabel serverStatus =
            new JLabel("● STARTING SERVER");
    private final JLabel totalLabel = new JLabel("0");
    private final JLabel activeLabel = new JLabel("0");
    private final JLabel latestLabel =
            new JLabel("Waiting for the first ambulance alert.");

    private ServerSocket serverSocket;
    private volatile boolean running = true;
    private int totalAlerts = 0;
    private int activeAmbulances = 0;

    public HospitalGUI() {
        setTitle("Smart Ambulance | Hospital Dashboard");
        setSize(1150, 700);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 247, 252));

        add(header(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(content(), BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                running = false;
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (Exception ignored) {
                }
            }
        });

        startServer();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(12, 57, 100));
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("✚  HOSPITAL EMERGENCY DASHBOARD");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 4));
        labels.setOpaque(false);
        labels.add(title);

        serverStatus.setForeground(new Color(153, 255, 196));
        serverStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        labels.add(serverStatus);

        p.add(labels, BorderLayout.CENTER);
        return p;
    }

    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(170, 100));
        p.setBackground(new Color(19, 47, 76));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 12, 20, 12));

        for (String item : new String[]{
                "▣  Dashboard", "♧  Alerts",
                "🚑  Ambulances", "⚙  Settings"}) {
            JLabel label = new JLabel(item);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            label.setBorder(new EmptyBorder(12, 8, 12, 4));
            p.add(label);
        }

        return p;
    }

    private JPanel content() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(statCard("TOTAL ALERTS", totalLabel,
                new Color(220, 55, 72)));
        stats.add(statCard("ACTIVE CONNECTIONS", activeLabel,
                new Color(17, 113, 199)));
        stats.add(statCard("SERVER PORT", new JLabel("5000"),
                new Color(15, 132, 81)));

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(223, 236, 247));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel tableCard = card("Incoming Emergency Alerts");
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel latest = card("Latest Alert Details");
        latestLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        latestLabel.setForeground(new Color(28, 66, 101));
        latest.add(latestLabel, BorderLayout.CENTER);

        JButton clear = new JButton("Clear Table");
        clear.addActionListener(e -> {
            model.setRowCount(0);
            totalAlerts = 0;
            totalLabel.setText("0");
        });
        latest.add(clear, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);
        bottom.add(latest, BorderLayout.CENTER);

        p.add(stats, BorderLayout.NORTH);
        p.add(tableCard, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel statCard(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout(5, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 224, 238)),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel heading = new JLabel(title);
        heading.setForeground(new Color(80, 101, 121));
        heading.setFont(new Font("SansSerif", Font.BOLD, 11));

        value.setForeground(color);
        value.setFont(new Font("SansSerif", Font.BOLD, 26));

        p.add(heading, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
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

    private void startServer() {
        Thread thread = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(5000)) {
                serverSocket = server;

                SwingUtilities.invokeLater(() ->
                        serverStatus.setText("● SERVER RUNNING | PORT 5000"));

                while (running) {
                    Socket client = server.accept();
                    SwingUtilities.invokeLater(() ->
                            activeLabel.setText("1"));

                    Thread clientThread = new Thread(() -> {
                        try (Socket s = client;
                             BufferedReader in = new BufferedReader(
                                     new InputStreamReader(
                                             s.getInputStream()))) {

                            String message = in.readLine();
                            if (message != null && !message.isBlank()) {
                                showAlert(message);
                            }
                        } catch (Exception ex) {
                            if (running) {
                                SwingUtilities.invokeLater(() ->
                                        serverStatus.setText(
                                                "● CLIENT CONNECTION ERROR"));
                            }
                        } finally {
                            SwingUtilities.invokeLater(() ->
                                    activeLabel.setText("0"));
                        }
                    });
                    clientThread.start();
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        serverStatus.setText(
                                "● SERVER ERROR: " + ex.getMessage()));
            }
        }, "Hospital-TCP-Server");

        thread.setDaemon(true);
        thread.start();
    }

    private void showAlert(String message) {
        String time = LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss"));

        String id = extract(message, "Ambulance ID:");
        String location = extract(message, "Current Location:");
        String emergency = extract(message, "Emergency Type:");
        String route = extract(message, "Fastest Route:");

        SwingUtilities.invokeLater(() -> {
            model.addRow(new Object[]{
                    time, id, location, emergency, route
            });

            totalAlerts++;
            totalLabel.setText(String.valueOf(totalAlerts));
            latestLabel.setText("<html><b>Received at:</b> " + time
                    + "<br><b>Ambulance:</b> " + id
                    + "<br><b>Location:</b> " + location
                    + "<br><b>Emergency:</b> " + emergency
                    + "<br><b>Route:</b> " + route + "</html>");

            serverStatus.setText("● ALERT RECEIVED");
        });
    }

    private String extract(String message, String key) {
        for (String field : message.split("\\s*\\|\\s*")) {
            if (field.startsWith(key)) {
                return field.substring(key.length()).trim();
            }
        }
        return "Not provided";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new HospitalGUI().setVisible(true));
    }
}
