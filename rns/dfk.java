import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShelterNet extends JFrame {

    static java.util.List<Request> requests = new ArrayList<>();

    CardLayout cl = new CardLayout();
    JPanel root = new JPanel(cl);

    JLabel warningText = new JLabel("CLEAR OF DISASTERS. HAVE A NICE DAY", SwingConstants.CENTER);

    boolean loggedIn = false;

    Color bg = new Color(15, 15, 20);
    Color card = new Color(30, 30, 40);
    Color accent = new Color(0, 200, 180);

    public ShelterNet() {

        setTitle("Shelter Net");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        load();

        root.add(home(), "home");
        root.add(help(), "help");
        root.add(login(), "login");
        root.add(donation(), "donation");
        root.add(warning(), "warning");

        add(root);
        cl.show(root, "home");

        setVisible(true);
    }

    // ================= HOME =================
    JPanel home() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);

        JLabel title = new JLabel("SHELTER NET", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 54));
        title.setForeground(Color.WHITE);

        JLabel tag = new JLabel("Humanity in Action", SwingConstants.CENTER);
        tag.setFont(new Font("Serif", Font.ITALIC, 24));
        tag.setForeground(Color.LIGHT_GRAY);

        JPanel top = new JPanel(new GridLayout(2,1));
        top.setBackground(bg);
        top.add(title);
        top.add(tag);

        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(320, 0));
        side.setBackground(new Color(25,25,25));

        warningText.setFont(new Font("Serif", Font.BOLD, 16));
        warningText.setForeground(Color.WHITE);

        side.add(warningText, BorderLayout.CENTER);

        JButton b1 = btn("REQUEST HELP");
        JButton b2 = btn("VOLUNTEER DASHBOARD");
        JButton b3 = btn("DONATION DESK");
        JButton b4 = btn("WARNING SYSTEM");

        b1.addActionListener(e -> cl.show(root, "help"));
        b2.addActionListener(e -> cl.show(root, "login"));
        b3.addActionListener(e -> cl.show(root, "donation"));
        b4.addActionListener(e -> {
            if (!loggedIn) {
                JOptionPane.showMessageDialog(this, "Login required");
                cl.show(root, "login");
            } else cl.show(root, "warning");
        });

        JPanel buttons = new JPanel();
        buttons.setBackground(bg);
        buttons.add(b1); buttons.add(b2); buttons.add(b3); buttons.add(b4);

        p.add(top, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.CENTER);
        p.add(side, BorderLayout.EAST);

        return p;
    }

    // ================= HELP (UNCHANGED CORE) =================
    JPanel help() {
        JPanel p = new JPanel(new GridLayout(12,1));
        p.setBackground(bg);

        JTextField name = new JTextField();
        JTextField loc = new JTextField();

        JCheckBox anon = new JCheckBox("Anonymous");

        JCheckBox food = new JCheckBox("Food");
        JCheckBox med = new JCheckBox("Medical");
        JCheckBox rescue = new JCheckBox("Rescue");
        JCheckBox shelter = new JCheckBox("Shelter");

        final String[] urgency = {""};

        JButton g = urgencyBtn("GREEN", Color.GREEN, urgency, "LOW");
        JButton y = urgencyBtn("YELLOW", Color.YELLOW, urgency, "MEDIUM");
        JButton r = urgencyBtn("RED", Color.RED, urgency, "HIGH");

        JButton submit = btn("SUBMIT REQUEST");

        JLabel err = new JLabel("", SwingConstants.CENTER);
        err.setForeground(Color.RED);

        p.add(label("Name"));
        p.add(name);
        p.add(anon);

        p.add(label("Location *"));
        p.add(loc);

        p.add(label("Help Type *"));
        JPanel hp = new JPanel();
        hp.setBackground(bg);
        hp.add(food); hp.add(med); hp.add(rescue); hp.add(shelter);
        p.add(hp);

        p.add(label("Urgency *"));
        JPanel up = new JPanel();
        up.setBackground(bg);
        up.add(g); up.add(y); up.add(r);
        p.add(up);

        p.add(err);
        p.add(submit);

        submit.addActionListener(e -> {

            if (loc.getText().isEmpty() || urgency[0].isEmpty()
                    || (!food.isSelected() && !med.isSelected()
                    && !rescue.isSelected() && !shelter.isSelected())) {
                err.setText("*needs to be filled");
                return;
            }

            String n = anon.isSelected() ? "Anonymous" : name.getText();

            String type = "";
            if (food.isSelected()) type += "Food ";
            if (med.isSelected()) type += "Medical ";
            if (rescue.isSelected()) type += "Rescue ";
            if (shelter.isSelected()) type += "Shelter ";

            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

            requests.add(new Request(n, loc.getText(), type, urgency[0], "Pending", time));

            JOptionPane.showMessageDialog(this, "Request submitted!");

            cl.show(root, "home");
        });

        return p;
    }

    // ================= LOGIN =================
    JPanel login() {
        JPanel p = new JPanel(new GridLayout(4,1));
        p.setBackground(bg);

        JTextField id = new JTextField();
        JPasswordField pass = new JPasswordField();

        JButton login = btn("LOGIN");

        p.add(label("ID"));
        p.add(id);
        p.add(label("Password (5 digits)"));
        p.add(pass);
        p.add(login);

        login.addActionListener(e -> {
            if (!new String(pass.getPassword()).matches("\\d{5}")) {
                JOptionPane.showMessageDialog(this, "Invalid login");
                return;
            }

            loggedIn = true;
            dashboard();
        });

        return p;
    }

    // ================= DASHBOARD =================
    void dashboard() {

        JPanel dash = new JPanel();
        dash.setLayout(new BoxLayout(dash, BoxLayout.Y_AXIS));
        dash.setBackground(bg);

        for (Request r : requests) {

            JPanel c = new JPanel(new GridLayout(5,1));
            c.setBackground(card);

            JLabel info = new JLabel(
                    "<html><font color='white'>"
                            + "Name: " + r.name + "<br>"
                            + "Location: " + r.location + "<br>"
                            + "Help: " + r.type + "<br>"
                            + "Urgency: " + r.urgency + "<br>"
                            + "Status: " + r.status
                            + "</font></html>"
            );

            JButton accept = btn("ACCEPT");

            accept.addActionListener(e -> {
                r.status = "Accepted";
                taskWindow(r);
                dashboard();
            });

            c.add(info);
            c.add(accept);
            dash.add(c);
        }

        JScrollPane sp = new JScrollPane(dash);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(sp, BorderLayout.CENTER);

        root.add(wrap, "dash");
        cl.show(root, "dash");
    }

    // ================= TASK =================
    void taskWindow(Request r) {

        JFrame f = new JFrame("Task");
        f.setSize(400,300);

        JCheckBox f1 = new JCheckBox("Food");
        JCheckBox f2 = new JCheckBox("Medical");
        JCheckBox f3 = new JCheckBox("Rescue");
        JCheckBox f4 = new JCheckBox("Shelter");

        JButton done = new JButton("FINISH");

        JLabel msg = new JLabel("", SwingConstants.CENTER);

        JPanel p = new JPanel(new GridLayout(6,1));

        p.add(f1);p.add(f2);p.add(f3);p.add(f4);
        p.add(done);p.add(msg);

        done.addActionListener(e -> {

            if (f1.isSelected() && f2.isSelected()
                    && f3.isSelected() && f4.isSelected()) {

                r.status = "Completed";
                msg.setText("COMPLETED");
                f.dispose();
                dashboard();

            } else {
                r.status = "Helped";
                msg.setText("HELPED");
            }
        });

        f.add(p);
        f.setVisible(true);
    }

    // ================= DONATION (FIXED) =================
    JPanel donation() {

        JPanel p = new JPanel(new GridLayout(6,1));
        p.setBackground(bg);

        JTextField name = new JTextField();
        JTextField loc = new JTextField();

        JButton food = btn("DONATE FOOD");
        JButton cloth = btn("DONATE CLOTHES");

        JButton submit = btn("SUBMIT");

        JLabel msg = new JLabel("",SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);

        boolean[] selected = {false};

        food.addActionListener(e -> selected[0] = true);
        cloth.addActionListener(e -> selected[0] = true);

        p.add(label("Name"));
        p.add(name);
        p.add(label("Pickup Location"));
        p.add(loc);
        p.add(food);
        p.add(cloth);
        p.add(submit);
        p.add(msg);

        submit.addActionListener(e -> {

            if (!selected[0]) {
                msg.setText("*Select donation type");
                return;
            }

            if (loc.getText().isEmpty()) {
                msg.setText("*Pickup required");
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Our volunteers are on the way. Thank you for your kindness.");

            cl.show(root,"home");
        });

        return p;
    }

    // ================= WARNING (FIXED) =================
    JPanel warning() {

        JPanel p = new JPanel(new GridLayout(5,1));

        JTextField name = new JTextField();
        JTextField time = new JTextField();

        JButton submit = btn("ISSUE WARNING");

        p.add(label("Disaster Name"));
        p.add(name);
        p.add(label("Time (Days/Hours)"));
        p.add(time);
        p.add(submit);

        submit.addActionListener(e -> {

            String msg = "ALERT! " + name.getText() + " will hit in " + time.getText();
            warningText.setText(msg);

            JOptionPane.showMessageDialog(this, "Warning Issued");

            cl.show(root,"home");
        });

        return p;
    }

    // ================= HELPERS =================
    JButton btn(String t){
        JButton b=new JButton(t);
        b.setFont(new Font("Serif",Font.BOLD,14));
        b.setBackground(accent);
        return b;
    }

    JButton urgencyBtn(String t,Color c,String[]u,String v){
        JButton b=new JButton(t);
        b.setBackground(c);
        b.addActionListener(e->u[0]=v);
        return b;
    }

    JLabel label(String t){
        JLabel l=new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Serif",Font.BOLD,16));
        return l;
    }

    public static void main(String[] args){
        new ShelterNet();
    }

    static class Request{
        String name,location,type,urgency,status,time;
        Request(String a,String b,String c,String d,String e,String f){
            name=a;location=b;type=c;urgency=d;status=e;time=f;
        }
    }
}