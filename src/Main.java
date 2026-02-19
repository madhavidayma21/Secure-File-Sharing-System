import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String url = "jdbc:mysql://localhost:3306/secure_file_db";
    static final String user = "root";
    static final String password = "Root12345"; // <-- Replace with your MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Register (Users only)");
        System.out.println("2. Login");
        System.out.print("Choose option: ");
        int choice = sc.nextInt();
        sc.nextLine(); // clear buffer

        if (choice == 1) {
            registerUser(sc);
        } else if (choice == 2) {
            loginUser(sc);
        } else {
            System.out.println("Invalid choice");
        }
    }

    // 🔐 Registration (user role only)
    public static void registerUser(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter password: ");
            String pass = sc.nextLine();

            String role = "user"; // role is fixed to user

            String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, pass);
            ps.setString(3, role);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ User Registered Successfully!");
            else
                System.out.println("❌ Registration Failed.");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔑 Login
    public static void loginUser(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter password: ");
            String pass = sc.nextLine();

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Login Successful!");
                String role = rs.getString("role");
                String uname = rs.getString("username");

                System.out.println("Role: " + role);

                int userId = rs.getInt("id");

                // Menu based on role
                if(role.equalsIgnoreCase("admin") && uname.equals("admin")) {
                    System.out.println("\n1. Upload File");
                    System.out.println("2. Download File");
                    System.out.println("3. Admin View");
                    System.out.println("4. Exit");
                } else {
                    System.out.println("\n1. Upload File");
                    System.out.println("2. Download File");
                    System.out.println("3. Exit");
                }

                System.out.print("Choose option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                if(choice == 1) uploadFile(sc, userId);
                else if(choice == 2) downloadFile(sc, userId);
                else if(choice == 3 && role.equalsIgnoreCase("admin") && uname.equals("admin")) adminView(sc);
                else System.out.println("Exiting...");

            } else {
                System.out.println("❌ Invalid Username or Password.");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📂 Upload File Entry
    public static void uploadFile(Scanner sc, int userId) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.print("Enter file name: ");
            String filename = sc.nextLine();

            System.out.print("Enter file path: ");
            String filepath = sc.nextLine();

            String sql = "INSERT INTO files(filename, filepath, uploaded_by) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, filename);
            ps.setString(2, filepath);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ File uploaded successfully!");
            else
                System.out.println("❌ Upload failed.");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📥 Download File + Log Entry
    public static void downloadFile(Scanner sc, int userId) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            // Display available files
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, filename, uploaded_by FROM files");

            System.out.println("\nAvailable Files:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("filename") +
                        ", Uploaded by UserID: " + rs.getInt("uploaded_by"));
            }

            System.out.print("\nEnter File ID to download: ");
            int fileId = sc.nextInt();
            sc.nextLine();

            // Insert log into download_logs table
            String logSql = "INSERT INTO download_logs(user_id, file_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(logSql);

            ps.setInt(1, userId);
            ps.setInt(2, fileId);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Download recorded successfully!");
            else
                System.out.println("❌ Download log failed.");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🖥️ Admin View — Files and Download Logs
    public static void adminView(Scanner sc) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("\n=== Uploaded Files ===");
            String filesSql = "SELECT f.id, f.filename, f.filepath, u.username AS uploaded_by " +
                    "FROM files f JOIN users u ON f.uploaded_by = u.id";
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(filesSql);

            while (rs1.next()) {
                System.out.println("ID: " + rs1.getInt("id") +
                        ", Name: " + rs1.getString("filename") +
                        ", Path: " + rs1.getString("filepath") +
                        ", Uploaded by: " + rs1.getString("uploaded_by"));
            }

            System.out.println("\n=== Download Logs ===");
            String logsSql = "SELECT dl.id, u.username AS downloaded_by, f.filename, dl.download_time " +
                    "FROM download_logs dl " +
                    "JOIN users u ON dl.user_id = u.id " +
                    "JOIN files f ON dl.file_id = f.id";
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(logsSql);

            while (rs2.next()) {
                System.out.println("Log ID: " + rs2.getInt("id") +
                        ", User: " + rs2.getString("downloaded_by") +
                        ", File: " + rs2.getString("filename") +
                        ", Time: " + rs2.getTimestamp("download_time"));
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
