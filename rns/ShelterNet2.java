import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShelterNet2 extends JFrame {

    static java.util.List<Request> requests = new ArrayList<>();

    CardLayout cl = new CardLayout();
    JPanel root = new JPanel(cl);

    JPanel dashboardPanel;

    Color bg = new Color(30, 42, 56);
    Color accent = new Color(0, 173, 181);

    // ================= MAIN =================
    public ShelterNet2() {

        setTitle("Shelter Net2 - Humanity in Action");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadData();

        root.add(homePage(), "home");
        root.add(helpPage(), "help");
        root.add(loginPage(), "login");

        add(root);

        cl.show(root, "home");
    }

    // ================= PAGE 1 =================
    JPanel homePage() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);

        JLabel title = new JLabel("Shelter Net2", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        JLabel tag = new JLabel("Humanity in Action", SwingConstants.CENTER);
        tag.setFont(new Font("Arial", Font.ITALIC, 20));
        tag.setForeground(Color.LIGHT_GRAY);

        JButton req = new JButton("Request Help");
        JButton vol = new JButton("Volunteer Dashboard");

        style(req);
        style(vol);

        req.addActionListener(e -> cl.show(root, "help"));
        vol.addActionListener(e -> cl.show(root, "login"));

        JPanel btn = new JPanel();
        btn.setBackground(bg);
        btn.add(req);
        btn.add(vol);

        p.add(title, BorderLayout.NORTH);
        p.add(tag, BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);

        return p;
    }

    // ================= PAGE 2 =================
    JPanel helpPage() {

        JPanel p = new JPanel(new GridLayout(12, 1));
        p.setBackground(bg);

        JTextField name = new JTextField();
        JTextField loc = new JTextField();

        JCheckBox anon = new JCheckBox("Anonymous");

        JCheckBox food = new JCheckBox("Food");
        JCheckBox med = new JCheckBox("Medical");
        JCheckBox rescue = new JCheckBox("Rescue");
        JCheckBox shelter = new JCheckBox("Shelter");

        final String[] urgency = {""};

        JButton g = new JButton("GREEN");
        JButton y = new JButton("YELLOW");
        JButton r = new JButton("RED");

        g.setBackground(Color.GREEN);
        y.setBackground(Color.YELLOW);
        r.setBackground(Color.RED);

        g.addActionListener(e -> urgency[0] = "LOW");
        y.addActionListener(e -> urgency[0] = "MEDIUM");
        r.addActionListener(e -> urgency[0] = "HIGH");

        JButton submit = new JButton("SUBMIT");

        JLabel error = new JLabel("", SwingConstants.CENTER);
        error.setForeground(Color.RED);

        p.add(lbl("Name"));
        p.add(name);
        p.add(anon);

        p.add(lbl("Location *"));
        p.add(loc);

        p.add(lbl("Help Type *"));
        JPanel hp = new JPanel();
        hp.add(food); hp.add(med); hp.add(rescue); hp.add(shelter);
        p.add(hp);

        p.add(lbl("Urgency *"));
        JPanel up = new JPanel();
        up.add(g); up.add(y); up.add(r);
        p.add(up);

        p.add(error);
        p.add(submit);

        submit.addActionListener(e -> {

            String n = anon.isSelected() ? "Anonymous" : name.getText();
            String l = loc.getText();

            if (l.isEmpty() || urgency[0].isEmpty()
                    || (!food.isSelected() && !med.isSelected()
                    && !rescue.isSelected() && !shelter.isSelected())) {

                error.setText("⚠ Fill all required fields (*)");
                return;
            }

            String type = "";
            if (food.isSelected()) type += "Food ";
            if (med.isSelected()) type += "Medical ";
            if (rescue.isSelected()) type += "Rescue ";
            if (shelter.isSelected()) type += "Shelter ";

            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

            requests.add(new Request(n, l, type, urgency[0], "Pending", time));

            saveData();

            JOptionPane.showMessageDialog(this,
                    "Request submitted successfully\nTime: " + time);

            cl.show(root, "home");
        });

        return p;
    }

    // ================= LOGIN =================
    JPanel loginPage() {

        JPanel p = new JPanel(new GridLayout(5, 1));

        JTextField id = new JTextField();
        JPasswordField pass = new JPasswordField();

        JButton login = new JButton("LOGIN");

        style(login);

        p.add(lbl("ID"));
        p.add(id);
        p.add(lbl("Password (5 digits)"));
        p.add(pass);
        p.add(login);

        login.addActionListener(e -> {

            if (id.getText().isEmpty()
                    || !new String(pass.getPassword()).matches("\\d{5}")) {
                JOptionPane.showMessageDialog(this, "Invalid login");
                return;
            }

            showDashboard();
        });

        return p;
    }

    // ================= DASHBOARD =================
    void showDashboard() {

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));

        refreshDashboard();

        JScrollPane sp = new JScrollPane(dashboardPanel);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(sp, BorderLayout.CENTER);

        root.add(wrap, "dash");
        cl.show(root, "dash");
    }

    void refreshDashboard() {

        dashboardPanel.removeAll();

        if (requests.isEmpty()) {
            JLabel empty = new JLabel("NO REQUESTS PENDING", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.BOLD, 26));
            dashboardPanel.add(empty);
            return;
        }

        for (Request r : requests) {

            if (r.status.equals("Completed")) continue;

            JPanel card = new JPanel(new GridLayout(6, 1));
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel info = new JLabel("<html>"
                    + "Name: " + r.name + "<br>"
                    + "Location: " + r.location + "<br>"
                    + "Help: " + r.type + "<br>"
                    + "Urgency: " + r.urgency + "<br>"
                    + "Status: " + r.status
                    + "</html>");

            JButton accept = new JButton("Accept Request");

            accept.addActionListener(e -> {
                r.status = "Accepted";
                saveData();
                openCompletion(r);
            });

            card.add(info);
            card.add(accept);

            dashboardPanel.add(card);
        }

        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    // ================= PAGE 4 =================
    void openCompletion(Request r) {

        JFrame f = new JFrame("Completion");
        f.setSize(450, 350);

        JCheckBox food = new JCheckBox("Food");
        JCheckBox med = new JCheckBox("Medical");
        JCheckBox rescue = new JCheckBox("Rescue");
        JCheckBox shelter = new JCheckBox("Shelter");

        JButton done = new JButton("Finish");

        JLabel status = new JLabel("", SwingConstants.CENTER);
        status.setFont(new Font("Arial", Font.BOLD, 20));
        status.setForeground(Color.GREEN);

        JPanel p = new JPanel(new GridLayout(6, 1));

        p.add(food);
        p.add(med);
        p.add(rescue);
        p.add(shelter);
        p.add(done);
        p.add(status);

        done.addActionListener(e -> {

            boolean all = food.isSelected() && med.isSelected()
                    && rescue.isSelected() && shelter.isSelected();

            if (all) {
                r.status = "Completed";
                saveData();

                status.setText("COMPLETED");

                dashboardPanel.removeAll();
                refreshDashboard();

                // ✅ FIXED TIMER (NO ERROR)
                new javax.swing.Timer(1500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        f.dispose();
                        cl.show(root, "home");
                    }
                }).start();

            } else {
                r.status = "Helped";
                saveData();
                status.setText("HELPED & FINISHED");
            }
        });

        f.add(p);
        f.setVisible(true);
    }

    // ================= HELPERS =================
    JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        return l;
    }

    void style(JButton b) {
        b.setBackground(accent);
        b.setFont(new Font("Arial", Font.BOLD, 14));
    }

    // ================= FILE =================
    void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data.txt"))) {
            for (Request r : requests) {
                pw.println(r.name + "|" + r.location + "|" + r.type + "|"
                        + r.urgency + "|" + r.status + "|" + r.time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadData() {
        File f = new File("data.txt");
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 6)
                    requests.add(new Request(p[0], p[1], p[2], p[3], p[4], p[5]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        new ShelterNet2().setVisible(true);
    }

    // ================= MODEL =================
    static class Request {
        String name, location, type, urgency, status, time;

        Request(String a, String b, String c, String d, String e, String f) {
            name = a;
            location = b;
            type = c;
            urgency = d;
            status = e;
            time = f;
        }
    }
}