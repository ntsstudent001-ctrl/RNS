import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ShelterNetApp extends JFrame {

    // DATA STORE
    static java.util.List<Request> requests = new ArrayList<>();

    // MAIN FRAME
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    // LOGIN FIELDS
    JTextField idField;
    JPasswordField passField;

    // DASHBOARD PANEL REFRESH
    JPanel dashboardPanel;

    // ===================== CONSTRUCTOR =====================
    public ShelterNetApp() {
        setTitle("Shelter Net - Humanity in Action");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadData();

        mainPanel.add(homePage(), "home");
        mainPanel.add(helpFormPage(), "form");
        mainPanel.add(loginPage(), "login");

        add(mainPanel);

        cardLayout.show(mainPanel, "home");
    }

    // ===================== PAGE 1: HOME =====================
    private JPanel homePage() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Shelter Net", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));

        JLabel tag = new JLabel("Humanity in Action", SwingConstants.CENTER);
        tag.setFont(new Font("Arial", Font.ITALIC, 20));

        JButton helpBtn = new JButton("Request Help");
        JButton volBtn = new JButton("Volunteer Dashboard");

        helpBtn.setBackground(Color.RED);
        helpBtn.setForeground(Color.WHITE);
        helpBtn.setFont(new Font("Arial", Font.BOLD, 18));

        volBtn.setBackground(Color.BLUE);
        volBtn.setForeground(Color.WHITE);
        volBtn.setFont(new Font("Arial", Font.BOLD, 18));

        helpBtn.addActionListener(e -> cardLayout.show(mainPanel, "form"));
        volBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(helpBtn);
        btnPanel.add(volBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(tag, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ===================== PAGE 2: HELP FORM =====================
    private JPanel helpFormPage() {

        JPanel panel = new JPanel(new GridLayout(10, 1, 10, 10));

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();

        JCheckBox anonymous = new JCheckBox("Submit as Anonymous");

        JCheckBox food = new JCheckBox("Food");
        JCheckBox medical = new JCheckBox("Medical");
        JCheckBox rescue = new JCheckBox("Rescue");
        JCheckBox shelter = new JCheckBox("Shelter");

        JPanel urgencyPanel = new JPanel();
        JButton green = new JButton("LOW");
        JButton yellow = new JButton("MEDIUM");
        JButton red = new JButton("HIGH");

        final String[] urgency = {""};

        green.setBackground(Color.GREEN);
        yellow.setBackground(Color.YELLOW);
        red.setBackground(Color.RED);

        green.addActionListener(e -> urgency[0] = "LOW");
        yellow.addActionListener(e -> urgency[0] = "MEDIUM");
        red.addActionListener(e -> urgency[0] = "HIGH");

        urgencyPanel.add(green);
        urgencyPanel.add(yellow);
        urgencyPanel.add(red);

        JButton submit = new JButton("SUBMIT REQUEST");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(anonymous);

        panel.add(new JLabel("Location *"));
        panel.add(locationField);

        panel.add(new JLabel("Help Type *"));
        JPanel helpTypePanel = new JPanel();
        helpTypePanel.add(food);
        helpTypePanel.add(medical);
        helpTypePanel.add(rescue);
        helpTypePanel.add(shelter);
        panel.add(helpTypePanel);

        panel.add(new JLabel("Urgency *"));
        panel.add(urgencyPanel);

        panel.add(submit);

        submit.addActionListener(e -> {

            String name = nameField.getText();
            String loc = locationField.getText();

            if (anonymous.isSelected()) {
                name = "Anonymous";
            }

            // VALIDATION
            if (loc.isEmpty() || urgency[0].isEmpty() ||
                    (!food.isSelected() && !medical.isSelected()
                            && !rescue.isSelected() && !shelter.isSelected())) {

                JOptionPane.showMessageDialog(this,
                        "Fill all mandatory fields (*)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String helpType = "";
            if (food.isSelected()) helpType += "Food ";
            if (medical.isSelected()) helpType += "Medical ";
            if (rescue.isSelected()) helpType += "Rescue ";
            if (shelter.isSelected()) helpType += "Shelter ";

            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

            Request r = new Request(name, loc, helpType, urgency[0], "Pending", time);
            requests.add(r);

            saveData();

            JOptionPane.showMessageDialog(this,
                    "Your request has been submitted successfully\nTime: " + time);

            cardLayout.show(mainPanel, "home");
        });

        return panel;
    }

    // ===================== PAGE 3: LOGIN =====================
    private JPanel loginPage() {
        JPanel panel = new JPanel(new GridLayout(5, 1));

        idField = new JTextField();
        passField = new JPasswordField();

        JButton loginBtn = new JButton("LOGIN");

        panel.add(new JLabel("Volunteer ID"));
        panel.add(idField);
        panel.add(new JLabel("Password (5 digits only)"));
        panel.add(passField);
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pass = new String(passField.getPassword());

            if (id.isEmpty() || pass.length() != 5 || !pass.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
                return;
            }

            showDashboard();
        });

        return panel;
    }

    // ===================== DASHBOARD =====================
    private void showDashboard() {

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        refreshDashboard();

        JScrollPane scroll = new JScrollPane(dashboardPanel);

        JButton completedView = new JButton("View Completed Requests");

        completedView.addActionListener(e -> showCompletedPage());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(completedView, BorderLayout.SOUTH);

        mainPanel.add(wrapper, "dashboard");
        cardLayout.show(mainPanel, "dashboard");
    }

    private void refreshDashboard() {

        dashboardPanel.removeAll();

        for (Request r : requests) {

            if (r.status.equals("Completed")) continue;

            JPanel card = new JPanel(new GridLayout(6, 1));
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel info = new JLabel(
                    "<html>Name: " + r.name +
                            "<br>Location: " + r.location +
                            "<br>Help: " + r.helpType +
                            "<br>Urgency: " + r.urgency +
                            "<br>Status: " + r.status + "</html>"
            );

            JButton accept = new JButton("ACCEPT REQUEST");

            accept.addActionListener(e -> {
                r.status = "Accepted";
                saveData();
                refreshDashboard();
                dashboardPanel.revalidate();
                dashboardPanel.repaint();
            });

            card.add(info);
            card.add(accept);

            dashboardPanel.add(card);
        }
    }

    // ===================== PAGE 4: COMPLETED =====================
    private void showCompletedPage() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Request r : requests) {
            if (!r.status.equals("Completed")) continue;

            panel.add(new JLabel(
                    r.name + " | " + r.location + " | " + r.helpType + " | " + r.status
            ));
        }

        JScrollPane scroll = new JScrollPane(panel);

        mainPanel.add(scroll, "completed");
        cardLayout.show(mainPanel, "completed");
    }

    // ===================== FILE STORAGE =====================
    private void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("requests.txt"))) {
            for (Request r : requests) {
                pw.println(r.name + "|" + r.location + "|" +
                        r.helpType + "|" + r.urgency + "|" +
                        r.status + "|" + r.time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        File f = new File("requests.txt");
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 6) {
                    requests.add(new Request(p[0], p[1], p[2], p[3], p[4], p[5]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShelterNetApp().setVisible(true);
        });
    }

    // ===================== MODEL =====================
    static class Request {
        String name, location, helpType, urgency, status, time;

        Request(String n, String l, String h, String u, String s, String t) {
            name = n;
            location = l;
            helpType = h;
            urgency = u;
            status = s;
            time = t;
        }
    }
}