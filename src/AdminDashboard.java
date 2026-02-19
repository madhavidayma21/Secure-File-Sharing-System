import javax.swing.*;
import java.awt.*;

public class AdminDashboard {

    public AdminDashboard(String username) {

        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(420, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(60, 30, 30));

        JLabel welcome = new JLabel("Admin: " + username);
        welcome.setBounds(150, 20, 200, 25);
        welcome.setForeground(Color.WHITE);
        panel.add(welcome);

        JButton uploadBtn = new JButton("Upload File");
        uploadBtn.setBounds(130, 60, 150, 35);
        panel.add(uploadBtn);

        JButton downloadBtn = new JButton("Download File");
        downloadBtn.setBounds(130, 105, 150, 35);
        panel.add(downloadBtn);

        JButton viewFilesBtn = new JButton("View All Files");
        viewFilesBtn.setBounds(130, 150, 150, 35);
        panel.add(viewFilesBtn);

        JButton logsBtn = new JButton("Download Logs");
        logsBtn.setBounds(130, 195, 150, 35);
        panel.add(logsBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(130, 240, 150, 35);
        panel.add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginUI();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}

