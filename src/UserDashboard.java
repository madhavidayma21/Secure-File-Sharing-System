import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserDashboard {

    String url = "jdbc:mysql://localhost:3306/secure_file_db";
    String dbUser = "root";
    String dbPass = "Root12345"; // ← replace

    public UserDashboard(String username) {

        JFrame frame = new JFrame("User Dashboard");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(40, 40, 70));

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setBounds(120, 20, 200, 25);
        welcome.setForeground(Color.WHITE);
        panel.add(welcome);

        JButton uploadBtn = new JButton("Upload File");
        uploadBtn.setBounds(120, 70, 150, 35);
        panel.add(uploadBtn);

        JButton downloadBtn = new JButton("Download File");
        downloadBtn.setBounds(120, 120, 150, 35);
        panel.add(downloadBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(120, 170, 150, 35);
        panel.add(logoutBtn);

        // ================= UPLOAD =================
        uploadBtn.addActionListener(e -> {

            String filename = JOptionPane.showInputDialog(frame, "Enter File Name:");
            String filepath = JOptionPane.showInputDialog(frame, "Enter File Path:");

            if (filename == null || filepath == null || filename.isEmpty() || filepath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required!");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

                // Get user ID
                String userSql = "SELECT id FROM users WHERE username=?";
                PreparedStatement userPs = conn.prepareStatement(userSql);
                userPs.setString(1, username);
                ResultSet rs = userPs.executeQuery();

                int userId = 0;
                if (rs.next()) {
                    userId = rs.getInt("id");
                }

                // Insert file
                String sql = "INSERT INTO files(filename, filepath, uploaded_by) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, filename);
                ps.setString(2, filepath);
                ps.setInt(3, userId);

                int rows = ps.executeUpdate();

                if (rows > 0)
                    JOptionPane.showMessageDialog(frame, "✅ File Uploaded Successfully!");
                else
                    JOptionPane.showMessageDialog(frame, "❌ Upload Failed!");

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database Error!");
            }
        });

        // ================= DOWNLOAD =================
        downloadBtn.addActionListener(e -> {

            try {
                Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

                // Show available files
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, filename FROM files");

                StringBuilder fileList = new StringBuilder("Available Files:\n");
                while (rs.next()) {
                    fileList.append("ID: ")
                            .append(rs.getInt("id"))
                            .append(" | ")
                            .append(rs.getString("filename"))
                            .append("\n");
                }

                JOptionPane.showMessageDialog(frame, fileList.toString());

                // Ask file ID
                String fileIdStr = JOptionPane.showInputDialog(frame, "Enter File ID to download:");
                if (fileIdStr == null || fileIdStr.isEmpty()) return;

                int fileId = Integer.parseInt(fileIdStr);

                // Get user ID
                String userSql = "SELECT id FROM users WHERE username=?";
                PreparedStatement userPs = conn.prepareStatement(userSql);
                userPs.setString(1, username);
                ResultSet userRs = userPs.executeQuery();

                int userId = 0;
                if (userRs.next()) {
                    userId = userRs.getInt("id");
                }

                // Insert download log
                String logSql = "INSERT INTO download_logs(user_id, file_id) VALUES (?, ?)";
                PreparedStatement logPs = conn.prepareStatement(logSql);
                logPs.setInt(1, userId);
                logPs.setInt(2, fileId);

                int rows = logPs.executeUpdate();

                if (rows > 0)
                    JOptionPane.showMessageDialog(frame, "✅ Download Recorded Successfully!");
                else
                    JOptionPane.showMessageDialog(frame, "❌ Download Failed!");

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error during download!");
            }
        });

        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginUI();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
