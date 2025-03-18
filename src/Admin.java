import java.sql.*;
import java.util.Scanner;

public class Admin extends User {
    private Scanner scanner;

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password);
        this.scanner = new Scanner(System.in);
    }

    public void menu(Connection conn) {
        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.println("-------------     Admin Menu     -------------   ");
            System.out.println("---------------------------------------------\n");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Instructors");
            System.out.println("3. Manage Courses");
            System.out.println("4. Manage Admins");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    manageStudents(conn);
                    break;
                case 2:
                    manageInstructors(conn);
                    break;
                case 3:
                    manageCourses(conn);
                    break;
                case 4:
                    manageAdmins(conn);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void manageStudents(Connection conn) {
        System.out.println("\n Manage Students \n");
        System.out.println("1. View Students");
        System.out.println("2. Add Student");
        System.out.println("3. Search Student");
        System.out.println("4. Edit Student");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                viewStudents(conn);
                break;
            case 2:
                addStudent(conn);
                break;
            case 3:
                searchStudent(conn);
                break;
            case 4:
                editStudent(conn);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void viewStudents(Connection conn) {
        try {
            String sql = "SELECT * FROM students";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                            ", Email: " + rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing students: " + e.getMessage());
        }
    }

    private void addStudent(Connection conn) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student email: ");
        String email = scanner.nextLine();
        System.out.print("Enter student password: ");
        String password = scanner.nextLine();

        try {
            String sql = "INSERT INTO students (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
                System.out.println("Student added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private void searchStudent(Connection conn) {
        System.out.print("Enter student name or email to search: ");
        String keyword = scanner.nextLine();

        try {
            String sql = "SELECT * FROM students WHERE name LIKE ? OR email LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                                ", Email: " + rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    private void editStudent(Connection conn) {
        System.out.print("Enter student ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        try {
            String sql = "UPDATE students SET name = ?, email = ?, password = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setInt(4, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Student updated successfully.");
                } else {
                    System.out.println("Student ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error editing student: " + e.getMessage());
        }
    }

    private void manageInstructors(Connection conn) {
        System.out.println("\n Manage Instructors \n");
        System.out.println("1. View Instructors");
        System.out.println("2. Add Instructor");
        System.out.println("3. Search Instructor");
        System.out.println("4. Approve Instructor");
        System.out.println("5. Edit Instructor");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewInstructors(conn);
                break;
            case 2:
                addInstructor(conn);
                break;
            case 3:
                searchInstructor(conn);
                break;
            case 4:
                approveInstructors(conn);
                break;
            case 5:
                editInstructors(conn);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void viewInstructors(Connection conn) {
        try {
            String sql = "SELECT * FROM instructors";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                            ", Email: " + rs.getString("email") + ", Approved: " + rs.getBoolean("approved"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing instructors: " + e.getMessage());
        }
    }

    private void addInstructor(Connection conn) {
        System.out.print("Enter instructor name: ");
        String name = scanner.nextLine();
        System.out.print("Enter instructor email: ");
        String email = scanner.nextLine();
        System.out.print("Enter instructor password: ");
        String password = scanner.nextLine();

        try {
            String sql = "INSERT INTO instructors (name, email, password, approved) VALUES (?, ?, ?, 0)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
                System.out.println("Instructor added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding instructor: " + e.getMessage());
        }
    }

    private void searchInstructor(Connection conn) {
        System.out.print("Enter instructor name or email to search: ");
        String keyword = scanner.nextLine();

        try {
            String sql = "SELECT * FROM instructors WHERE name LIKE ? OR email LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                                ", Email: " + rs.getString("email") + ", Approved: " + rs.getBoolean("approved"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching instructor: " + e.getMessage());
        }
    }

    private void approveInstructors(Connection conn) {
        System.out.println("\nUnapproved Instructors:");
        try {
            String sql = "SELECT * FROM instructors WHERE approved = 0";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                boolean hasUnapproved = false;
                while (rs.next()) {
                    hasUnapproved = true;
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                            ", Email: " + rs.getString("email"));
                }
                if (!hasUnapproved) {
                    System.out.println("No unapproved instructors found.");
                    return; // Exit if no unapproved instructors
                }
            }

            System.out.print("\nEnter instructor ID to approve: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String updateSql = "UPDATE instructors SET approved = 1 WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Instructor approved successfully.");
                } else {
                    System.out.println("Instructor ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error approving instructor: " + e.getMessage());
        }
    }

    private void editInstructors(Connection conn) {
        System.out.println("\nAll Instructors:");
        try {
            String sql = "SELECT * FROM instructors";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                            ", Email: " + rs.getString("email") + ", Approved: " + (rs.getBoolean("approved") ? "Yes" : "No"));
                }
            }

            System.out.print("\nEnter instructor ID to edit: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new name (or leave blank to keep current): ");
            String name = scanner.nextLine();
            System.out.print("Enter new email (or leave blank to keep current): ");
            String email = scanner.nextLine();

            StringBuilder sql1 = new StringBuilder("UPDATE instructors SET ");
            if (!name.isEmpty()) sql1.append("name = ?, ");
            if (!email.isEmpty()) sql1.append("email = ?, ");
            sql1.deleteCharAt(sql1.length() - 2); // Remove trailing comma
            sql1.append(" WHERE id = ?");

            try (PreparedStatement pstmt = conn.prepareStatement(sql1.toString())) {
                int index = 1;
                if (!name.isEmpty()) pstmt.setString(index++, name);
                if (!email.isEmpty()) pstmt.setString(index++, email);
                pstmt.setInt(index, id);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Instructor updated successfully.");
                } else {
                    System.out.println("Instructor ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error editing instructor: " + e.getMessage());
        }
    }


    private void manageCourses(Connection conn) {
        System.out.println("\n Manage Courses \n");
        System.out.println("1. View Courses");
        System.out.println("2. Add Course");
        System.out.println("3. Search Course");
        System.out.println("4. Edit Course");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewCourses(conn);
                break;
            case 2:
                addCourse(conn);
                break;
            case 3:
                searchCourse(conn);
                break;
            case 4:
                editCourse(conn);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void viewCourses(Connection conn) {
        try {
            String sql = "SELECT * FROM courses";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                            ", Instructor ID: " + rs.getInt("instructor_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing courses: " + e.getMessage());
        }
    }

    private void addCourse(Connection conn) {
        System.out.print("Enter course name: ");
        String name = scanner.nextLine();
        System.out.print("Enter instructor ID: ");
        int instructorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String sql = "INSERT INTO courses (name, instructor_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, instructorId);
                pstmt.executeUpdate();
                System.out.println("Course added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    private void searchCourse(Connection conn) {
        System.out.print("Enter course name to search: ");
        String keyword = scanner.nextLine();

        try {
            String sql = "SELECT * FROM courses WHERE name LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                                ", Instructor ID: " + rs.getInt("instructor_id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching course: " + e.getMessage());
        }
    }

    private void editCourse(Connection conn) {
        System.out.print("Enter course ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new instructor ID: ");
        int instructorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String sql = "UPDATE courses SET name = ?, instructor_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, instructorId);
                pstmt.setInt(3, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Course updated successfully.");
                } else {
                    System.out.println("Course ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error editing course: " + e.getMessage());
        }
    }

    private void manageAdmins(Connection conn) {
        System.out.println("\n Manage Admins \n");
        System.out.println("1. Add New Admin");
        System.out.println("2. Delete Admin");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                addAdmin(conn);
                break;
            case 2:
                deleteAdmin(conn);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addAdmin(Connection conn) {
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine();
        System.out.print("Enter admin email: ");
        String email = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        try {
            String sql = "INSERT INTO admin (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
                System.out.println("Admin added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding admin: " + e.getMessage());
        }
    }

    private void deleteAdmin(Connection conn) {
        System.out.print("Enter admin ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String sql = "DELETE FROM admin WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Admin deleted successfully.");
                } else {
                    System.out.println("Admin ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting admin: " + e.getMessage());
        }
    }
}
