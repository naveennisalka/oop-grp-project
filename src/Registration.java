import com.mysql.jdbc.Connection;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Registration{
    public static void userRegistration(int userType){
    Connection conn = DatabaseConnection.connect();
    String checkSql = "";
    String insertSql = "";

    Scanner scanner = new Scanner(System.in);
        switch (userType) {
        case 3:
            System.out.print("Enter full name : ");
            String studentName = scanner.nextLine();
            String studentEmail = loginRegistation.getEmail(scanner);
            String studentPassword = loginRegistation.getPassword(scanner);

            checkSql = "SELECT * FROM students WHERE email = ?";
            insertSql = "INSERT INTO students(name, email, password) VALUES(?, ?, ?)";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, studentEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Email already registered. Please use a different email.");
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, studentName);
                        insertStmt.setString(2, studentEmail);
                        insertStmt.setString(3, studentPassword);
                        insertStmt.executeUpdate();
                        System.out.println("Student registration successful. You can now log in.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Registration failed: " + e.getMessage());
            }
            break;

        case 2:
            System.out.print("Enter full name : ");
            String instructorName = scanner.nextLine();
            String instructorEmail = loginRegistation.getEmail(scanner);
            String instructorPassword = loginRegistation.getPassword(scanner);

            checkSql = "SELECT * FROM instructors WHERE email = ?";
            insertSql = "INSERT INTO instructors(name, email, password) VALUES(?, ?, ?)";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, instructorEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Email already registered. Please use a different email.");
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, instructorName);
                        insertStmt.setString(2, instructorEmail);
                        insertStmt.setString(3, instructorPassword);
                        insertStmt.executeUpdate();
                        System.out.println("Instructor registration successful. Approval may be required.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Registration failed: " + e.getMessage());
            }
            break;

        case 1:
            System.out.print("Enter full name : ");
            String adminName = scanner.nextLine();
            String adminEmail = loginRegistation.getEmail(scanner);
            String adminPassword = loginRegistation.getPassword(scanner);

            checkSql = "SELECT * FROM admin WHERE email = ?";
            insertSql = "INSERT INTO admin(name, email, password) VALUES(?, ?, ?)";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, adminEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Email already registered. Please use a different email.");
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, adminName);
                        insertStmt.setString(2, adminEmail);
                        insertStmt.setString(3, adminPassword);
                        insertStmt.executeUpdate();
                        System.out.println("Admin registration successful. You can now log in.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Registration failed: " + e.getMessage());
            }
            break;

        default:
            System.out.println("Invalid user type. Please try again.");
    }


    }
}

