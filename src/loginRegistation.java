import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class loginRegistation {

    protected static void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*********************************************");
        System.out.println("*                                           *");
        System.out.println("*               🔒  LOGIN  🔒               *");
        System.out.println("*                                           *");
        System.out.println("*********************************************\n");
        System.out.print("| 1. admin | 2. instructor | 3.student  \nEnter user type  >>>  ");
        int userType = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();


        if (!validation.isValidEmail(email)) {
            System.out.println("Invalid email format. Please try again.");
            return;
        }

        Connection conn = DatabaseConnection.connect();
        String sql = "";

        switch (userType) {
            case 3:
                sql = "SELECT * FROM students WHERE email = ? AND password = ?";
                break;
            case 2:
                sql = "SELECT * FROM instructors WHERE email = ? AND password = ? AND approved = 1";
                break;
            case 1:
                sql = "SELECT * FROM admin WHERE email = ? AND password = ?";
                break;
            default:
                System.out.println("Invalid user type. Please try again.");
                return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                System.out.println("\n\n---------------------------------------------\n\n");
                System.out.println("Welcome, " + name + "!");

                if (userType == 3) {
                    Student student = new Student(id, name, email, password);
                    student.menu(conn);
                } else if (userType == 2) {
                    Instructor instructor = new Instructor(id, name, email, password);
                    instructor.menu(conn);
                } else if (userType == 1) {
                    Admin admin = new Admin(id, name, email, password);
                    admin.menu(conn);
                }
            } else {
                System.out.println("Invalid login details or approval pending. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    protected static void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*********************************************");
        System.out.println("*                                           *");
        System.out.println("*           📝  REGISTER  📝              *");
        System.out.println("*                                           *");
        System.out.println("*********************************************\n");

        System.out.print("| 1. admin | 2. instructor | 3.student  \nEnter user type  >>>  ");
        int userType = scanner.nextInt();

        Connection conn = DatabaseConnection.connect();
        String checkSql = "";
        String insertSql = "";

        Registration.userRegistration(userType);

    }

    static String getPassword(Scanner scanner) {
        while (true) {
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (!validation.isValidPassword(password)) {
                System.out.println("Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one number, and one special character.");
                continue;
            }

            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();

            if (password.equals(confirmPassword)) {
                return password;
            } else {
                System.out.println("Passwords do not match. Please try again.");
            }
        }
    }

    static String getEmail(Scanner scanner) {
        while (true) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            if (validation.isValidEmail(email)) {
                return email;
            } else {
                System.out.println("Invalid email format. Please try again.");
            }
        }
    }


}



