import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginUI {

    public LoginUI() {
        JFrame frame = new JFrame("Secure File Sharing System");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        panel.setLayout(null);

        JLabel title = new JLabel("Login");
        title.setBounds(160, 20, 100, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 25);
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 80, 180, 25);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 25);
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 120, 180, 25);
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(70, 180, 100, 35);
        panel.add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(200, 180, 100, 35);
        panel.add(registerBtn);

        JLabel message = new JLabel("");
        message.setBounds(90, 230, 250, 25);
        message.setForeground(Color.YELLOW);
        panel.add(message);

        // LOGIN ACTION
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter all fields");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/secure_file_db",
                        "root",
                        "Root12345" // replace with your password
                );

                String sql = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");

                    frame.dispose();

                    if (role.equalsIgnoreCase("admin")) {
                        new AdminDashboard(username);
                    } else {
                        new UserDashboard(username);
                    }

                } else {
                    message.setText("Invalid Username or Password ❌");
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Database Error!");
            }
        });

        registerBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "Register from console first.")
        );

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
