import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ShelterNet {

    // ---------------- DATA MODEL ----------------
    static class HelpRequest {
        String name;
        String urgency;
        String location;
        boolean food, medical, rescue, shelter;
        String status = "PENDING";

        boolean foodDone = false;
        boolean medicalDone = false;
        boolean rescueDone = false;
        boolean shelterDone = false;

        HelpRequest(String name, String urgency, String location,
                    boolean food, boolean medical, boolean rescue, boolean shelter) {
            this.name = name;
            this.urgency = urgency;
            this.location = location;
            this.food = food;
            this.medical = medical;
            this.rescue = rescue;
            this.shelter = shelter;
        }

        boolean isCompleted() {
            if (food && !foodDone) return false;
            if (medical && !medicalDone) return false;
            if (rescue && !rescueDone) return false;
            if (shelter && !shelterDone) return false;
            return true;
        }

        int progressValue() {
            int total = 0, done = 0;

            if (food) { total++; if (foodDone) done++; }
            if (medical) { total++; if (medicalDone) done++; }
            if (rescue) { total++; if (rescueDone) done++; }
            if (shelter) { total++; if (shelterDone) done++; }

            return total == 0 ? 0 : (done * 100 / total);
        }
    }

    static ArrayList<HelpRequest> requests = new ArrayList<>();
    static JLabel disasterLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShelterNet::createHome);
    }

    public static void createHome() {
        JFrame frame = new JFrame("Shelter Net");
        frame.setSize(650, 450);
        frame.setLayout(new BorderLayout());

        JPanel bg = new JPanel(new BorderLayout());
        bg.setBackground(new Color(230, 240, 250));
        frame.setContentPane(bg);

        JLabel title = new JLabel("SHELTER NET", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(new Color(20, 60, 120));
        bg.add(title, BorderLayout.NORTH);

        disasterLabel = new JLabel("  STATUS: NORMAL  ");
        disasterLabel.setOpaque(true);
        disasterLabel.setBackground(new Color(144, 238, 144));
        disasterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setBackground(new Color(210, 230, 245));
        status.add(disasterLabel);
        bg.add(status, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(3,1,15,15));
        center.setBackground(new Color(230, 240, 250));

        JButton help = new JButton("🚨 Request Help");
        JButton vol = new JButton("🧑‍🚒 Volunteer Dashboard");
        JButton don = new JButton("🎁 Donation Desk");

        style(help, new Color(255, 99, 71));
        style(vol, new Color(70, 130, 180));
        style(don, new Color(46, 139, 87));

        center.add(help);
        center.add(vol);
        center.add(don);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(new Color(230, 240, 250));
        wrap.add(center);

        bg.add(wrap, BorderLayout.CENTER);

        help.addActionListener(e -> helpForm());

        // 🔐 CHANGED: now opens login first
        vol.addActionListener(e -> volunteerLogin());

        don.addActionListener(e -> donationDesk());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void style(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
    }

    // 🔐 LOGIN METHOD ADDED
    public static void volunteerLogin() {
        JFrame loginFrame = new JFrame("Volunteer Login");
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3,2,5,5));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        style(loginBtn, new Color(70, 130, 180));

        loginFrame.add(new JLabel("ID:"));
        loginFrame.add(userField);

        loginFrame.add(new JLabel("Password:"));
        loginFrame.add(passField);

        loginFrame.add(new JLabel(""));
        loginFrame.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String id = userField.getText();
            String pass = new String(passField.getPassword());

            // ID = anything (not empty)
            // Password = exactly 5 digits
            if (!id.isEmpty() && pass.matches("\\d{5}")) {
                JOptionPane.showMessageDialog(loginFrame, "Login Successful!");
                loginFrame.dispose();
                volunteerDashboard();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid ID or Password (5-digit required)");
            }
        });

        loginFrame.setVisible(true);
    }

    public static void helpForm() {
        JFrame f = new JFrame("Request Help");
        f.setSize(400, 400);
        f.setLayout(new GridLayout(8,2,5,5));

        JTextField name = new JTextField();
        JTextField loc = new JTextField();

        String[] urgency = {"LOW", "MEDIUM", "HIGH"};
        JComboBox<String> urg = new JComboBox<>(urgency);

        JCheckBox food = new JCheckBox("Food");
        JCheckBox medical = new JCheckBox("Medical");
        JCheckBox rescue = new JCheckBox("Rescue");
        JCheckBox shelter = new JCheckBox("Shelter");

        JButton submit = new JButton("Submit");
        style(submit, new Color(46, 139, 87));

        f.add(new JLabel("Name:")); f.add(name);
        f.add(new JLabel("Urgency:")); f.add(urg);
        f.add(new JLabel("Location:")); f.add(loc);

        f.add(food); f.add(medical);
        f.add(rescue); f.add(shelter);

        f.add(new JLabel("")); f.add(submit);

        submit.addActionListener(e -> {
            if (loc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(f, "Location required!");
                return;
            }

            HelpRequest r = new HelpRequest(
                    name.getText().isEmpty() ? "Anonymous" : name.getText(),
                    (String) urg.getSelectedItem(),
                    loc.getText(),
                    food.isSelected(),
                    medical.isSelected(),
                    rescue.isSelected(),
                    shelter.isSelected()
            );

            requests.add(r);
            JOptionPane.showMessageDialog(f, "Request submitted!");
            f.dispose();
        });

        f.setVisible(true);
    }

    public static void volunteerDashboard() {
        JFrame frame = new JFrame("Volunteer Dashboard");
        frame.setSize(700, 600);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(235, 245, 255));

        for (int i = 0; i < requests.size(); i++) {

            HelpRequest r = requests.get(i);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(10,10,10,10)
            ));

            if (r.urgency.equals("HIGH")) card.setBackground(new Color(255, 200, 200));
            else if (r.urgency.equals("MEDIUM")) card.setBackground(new Color(255, 235, 180));
            else card.setBackground(new Color(200, 255, 200));

            JLabel header = new JLabel("ID #" + (i+1) + " - " + r.name);
            JLabel loc = new JLabel("📍 " + r.location);
            JLabel status = new JLabel("Status: " + r.status);

            JProgressBar bar = new JProgressBar(0,100);
            bar.setValue(r.progressValue());
            bar.setStringPainted(true);

            JCheckBox food = new JCheckBox("Food");
            JCheckBox medical = new JCheckBox("Medical");
            JCheckBox rescue = new JCheckBox("Rescue");
            JCheckBox shelter = new JCheckBox("Shelter");

            food.setSelected(r.foodDone);
            medical.setSelected(r.medicalDone);
            rescue.setSelected(r.rescueDone);
            shelter.setSelected(r.shelterDone);

            food.setEnabled(r.food);
            medical.setEnabled(r.medical);
            rescue.setEnabled(r.rescue);
            shelter.setEnabled(r.shelter);

            ActionListener update = e -> {
                r.foodDone = food.isSelected();
                r.medicalDone = medical.isSelected();
                r.rescueDone = rescue.isSelected();
                r.shelterDone = shelter.isSelected();

                bar.setValue(r.progressValue());

                if (r.isCompleted()) {
                    r.status = "COMPLETED";
                    status.setText("Status: COMPLETED");
                }
            };

            food.addActionListener(update);
            medical.addActionListener(update);
            rescue.addActionListener(update);
            shelter.addActionListener(update);

            header.setFont(new Font("Segoe UI", Font.BOLD, 16));

            card.add(header);
            card.add(loc);
            card.add(status);
            card.add(bar);
            card.add(food);
            card.add(medical);
            card.add(rescue);
            card.add(shelter);

            container.add(card);
            container.add(Box.createRigidArea(new Dimension(0,10)));
        }

        JScrollPane scroll = new JScrollPane(container);
        frame.add(scroll);

        frame.setVisible(true);
    }

    public static void donationDesk() {
        JFrame f = new JFrame("Donation Desk");
        f.setSize(400,300);
        f.setLayout(new GridLayout(6,2));

        JTextField name = new JTextField();
        JTextField id = new JTextField();

        JCheckBox food = new JCheckBox("Food");
        JCheckBox clothes = new JCheckBox("Clothes");
        JCheckBox shelter = new JCheckBox("Shelter");

        JButton donate = new JButton("Donate");
        style(donate, new Color(46, 139, 87));

        f.add(new JLabel("Name:")); f.add(name);
        f.add(new JLabel("ID:")); f.add(id);

        f.add(food); f.add(clothes); f.add(shelter);

        f.add(new JLabel("")); f.add(donate);

        donate.addActionListener(e -> {
            if (name.getText().isEmpty() || id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(f, "ID required!");
                return;
            }

            JOptionPane.showMessageDialog(f, "Donation registered!");
            f.dispose();
        });

        f.setVisible(true);
    }
}
