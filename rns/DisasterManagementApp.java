import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class DisasterManagementApp extends JFrame {

    private ArrayList<Incident> incidents = new ArrayList<>();
    private DefaultTableModel tableModel;

    // Constructor
    public DisasterManagementApp() {
        setTitle("Disaster Management & Emergency Response");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("SOS Emergency", createSOSPanel());
        tabs.add("Report Disaster", createReportPanel());
        tabs.add("Incident Dashboard", createDashboardPanel());

        add(tabs);

        loadData(); // load saved incidents
    }

    // ---------------- SOS PANEL ----------------
    private JPanel createSOSPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Emergency SOS", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.RED);

        JButton sosButton = new JButton("SEND SOS ALERT");
        sosButton.setFont(new Font("Arial", Font.BOLD, 24));
        sosButton.setBackground(Color.RED);
        sosButton.setForeground(Color.WHITE);

        sosButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "🚨 SOS SENT!\nNearest authorities notified (simulated).",
                    "Emergency Alert",
                    JOptionPane.ERROR_MESSAGE);

            addIncident(new Incident("SOS ALERT", "High Emergency", "Auto-generated SOS"));
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(sosButton, BorderLayout.CENTER);

        return panel;
    }

    // ---------------- REPORT PANEL ----------------
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField titleField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{
                "Earthquake", "Flood", "Fire", "Accident", "Other"
        });
        JTextArea descArea = new JTextArea();

        JButton submit = new JButton("Report Incident");

        panel.add(new JLabel("Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Type:"));
        panel.add(typeBox);

        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descArea));

        panel.add(new JLabel(""));
        panel.add(submit);

        submit.addActionListener(e -> {
            String title = titleField.getText();
            String type = (String) typeBox.getSelectedItem();
            String desc = descArea.getText();

            if (title.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            addIncident(new Incident(title, type, desc));

            JOptionPane.showMessageDialog(this, "Incident Reported Successfully!");

            titleField.setText("");
            descArea.setText("");
        });

        return panel;
    }

    // ---------------- DASHBOARD ----------------
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Title", "Type", "Description"};

        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scroll = new JScrollPane(table);

        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear All");

        JPanel bottom = new JPanel();
        bottom.add(refresh);
        bottom.add(clear);

        refresh.addActionListener(e -> refreshTable());

        clear.addActionListener(e -> {
            incidents.clear();
            refreshTable();
            saveData();
        });

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        refreshTable();

        return panel;
    }

    // ---------------- CORE FUNCTIONS ----------------
    private void addIncident(Incident i) {
        incidents.add(i);
        refreshTable();
        saveData();
    }

    private void refreshTable() {
        if (tableModel == null) return;

        tableModel.setRowCount(0);
        for (Incident i : incidents) {
            tableModel.addRow(new Object[]{
                    i.title, i.type, i.description
            });
        }
    }

    // ---------------- FILE SAVE ----------------
    private void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("incidents.txt"))) {
            for (Incident i : incidents) {
                pw.println(i.title + "|" + i.type + "|" + i.description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- FILE LOAD ----------------
    private void loadData() {
        File file = new File("incidents.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    incidents.add(new Incident(parts[0], parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DisasterManagementApp().setVisible(true);
        });
    }

    // ---------------- MODEL ----------------
    static class Incident {
        String title;
        String type;
        String description;

        Incident(String t, String ty, String d) {
            title = t;
            type = ty;
            description = d;
        }
    }
}