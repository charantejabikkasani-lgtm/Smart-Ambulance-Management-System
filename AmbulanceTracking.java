
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AmbulanceTracking extends JFrame {

    private final JLabel status = new JLabel("● READY TO DEPART");
    private final JLabel location = new JLabel("Ambulance Base");
    private final JLabel progress = new JLabel("0% complete");
    private final JProgressBar bar = new JProgressBar(0, 100);
    private final TrackingMap map = new TrackingMap();

    private final String[] route = {"Ambulance", "B", "Hospital"};
    private int step = 0;
    private Timer timer;

    public AmbulanceTracking() {
        setTitle("Smart Ambulance | Live Tracking");
        setSize(950, 620);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 247, 252));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 57, 100));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("⌖  LIVE AMBULANCE TRACKING");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        JPanel side = new JPanel();
        side.setBackground(new Color(19, 47, 76));
        side.setPreferredSize(new Dimension(235, 100));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel infoTitle = new JLabel("TRACKING STATUS");
        infoTitle.setForeground(new Color(184, 220, 247));
        infoTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        side.add(infoTitle);
        side.add(Box.createVerticalStrut(20));

        status.setForeground(new Color(122, 255, 177));
        status.setFont(new Font("SansSerif", Font.BOLD, 14));
        side.add(status);
        side.add(Box.createVerticalStrut(25));

        addInfo(side, "Current Location", location);
        addInfo(side, "Route Progress", progress);

        bar.setStringPainted(true);
        bar.setForeground(new Color(25, 174, 111));
        bar.setBackground(new Color(224, 236, 246));
        side.add(Box.createVerticalStrut(10));
        side.add(bar);
        side.add(Box.createVerticalStrut(25));

        JButton start = new JButton("▶  Start Tracking");
        start.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.addActionListener(e -> startTracking());
        side.add(start);

        JButton reset = new JButton("↺  Reset");
        reset.setAlignmentX(Component.LEFT_ALIGNMENT);
        reset.addActionListener(e -> resetTracking());
        side.add(Box.createVerticalStrut(10));
        side.add(reset);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel mapTitle = new JLabel("Ambulance Route");
        mapTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        mapTitle.setForeground(new Color(20, 57, 91));

        JPanel mapCard = new JPanel(new BorderLayout(8, 8));
        mapCard.setBackground(Color.WHITE);
        mapCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        mapCard.add(mapTitle, BorderLayout.NORTH);
        mapCard.add(map, BorderLayout.CENTER);

        JLabel note = new JLabel(
                "Simulation mode • Each stop takes 2 seconds");
        note.setForeground(new Color(88, 108, 126));

        center.add(mapCard, BorderLayout.CENTER);
        center.add(note, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(side, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timer != null) {
                    timer.stop();
                }
            }
        });
    }

    private void addInfo(JPanel panel, String title, JLabel value) {
        JLabel heading = new JLabel(title);
        heading.setForeground(new Color(184, 220, 247));
        heading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        value.setForeground(Color.WHITE);
        value.setFont(new Font("SansSerif", Font.BOLD, 15));

        panel.add(heading);
        panel.add(Box.createVerticalStrut(5));
        panel.add(value);
        panel.add(Box.createVerticalStrut(18));
    }

    private void startTracking() {
        if (timer != null && timer.isRunning()) {
            return;
        }

        if (step >= route.length) {
            resetTracking();
        }

        status.setText("● AMBULANCE EN ROUTE");
        timer = new Timer(2000, e -> {
            if (step < route.length) {
                location.setText(route[step]);
                map.setStep(step);
                step++;

                int percentage = (int) (
                        (step * 100.0) / route.length);
                bar.setValue(percentage);
                progress.setText(percentage + "% complete");
            } else {
                timer.stop();
                status.setText("● ARRIVED AT HOSPITAL");
                location.setText("Hospital");
                progress.setText("100% complete");
                bar.setValue(100);
            }
        });
        timer.start();
    }

    private void resetTracking() {
        if (timer != null) {
            timer.stop();
        }
        step = 0;
        location.setText("Ambulance Base");
        progress.setText("0% complete");
        bar.setValue(0);
        status.setText("● READY TO DEPART");
        map.setStep(-1);
    }

    private static class TrackingMap extends JPanel {
        private int step = -1;

        TrackingMap() {
            setBackground(new Color(239, 247, 250));
            setPreferredSize(new Dimension(500, 370));
        }

        void setStep(int step) {
            this.step = step;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int[][] points = {{80, 250}, {240, 115}, {430, 235}};

            g.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g.setColor(new Color(210, 223, 232));
            g.drawLine(80, 250, 240, 115);
            g.drawLine(240, 115, 430, 235);

            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g.setColor(new Color(24, 175, 105));
            g.drawLine(80, 250, 240, 115);
            g.drawLine(240, 115, 430, 235);

            String[] labels = {"Ambulance Base", "Road B", "Hospital"};

            for (int i = 0; i < points.length; i++) {
                int x = points[i][0], y = points[i][1];
                g.setColor(i == 2 ? new Color(224, 52, 65)
                        : new Color(15, 116, 207));
                g.fillOval(x - 16, y - 16, 32, 32);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 13));
                String mark = i == 2 ? "H" : String.valueOf(i + 1);
                g.drawString(mark, x - 4, y + 5);

                g.setColor(new Color(30, 55, 77));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                g.drawString(labels[i], x - 40, y + 34);
            }

            if (step >= 0 && step < points.length) {
                int x = points[step][0], y = points[step][1];
                g.setColor(new Color(255, 172, 35));
                g.fillOval(x - 11, y - 11, 22, 22);
                g.setColor(Color.WHITE);
                g.drawString("A", x - 4, y + 5);
            }

            g.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AmbulanceTracking().setVisible(true));
    }
}
