import java.sql.*;
import java.util.Scanner;

public class Instructor extends User {
    private Scanner scanner;

    public Instructor(int id, String name, String email, String password) {
        super(id, name, email, password);
        this.scanner = new Scanner(System.in);
    }

    public void menu(Connection conn) {
        while (true) {
            System.out.println("\nInstructor Menu");
            System.out.println("\n---------------------------------------------");
            System.out.println("-------------   Instructor Menu   -------------   ");
            System.out.println("---------------------------------------------\n");
            System.out.println("1. Edit Details");
            System.out.println("2. View Student Results");
            System.out.println("3. Add Quiz");
            System.out.println("4. Add/Edit Course");
            System.out.println("5. View Enrolled Students");
            System.out.println("6. Send/View Messages");
            System.out.println("7. Manage Quizzes And Questions");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    editDetails();
                    break;
                case 2:
                    viewStudentResults();
                    break;
                case 3:
                    addQuiz(conn);
                    break;
                case 4:
                    manageCourses(conn);
                    break;
                case 5:
                    viewEnrolledStudents(conn);
                    break;
                case 6:
                    manageMessages();
                    break;
                case 7:
                    manageQuizzesAndQuestions(conn);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void editDetails() {
        System.out.println("\nEdit Details");
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        String newEmail = getEmail(scanner);
        String newPassword = getPassword(scanner);

        Connection conn = DatabaseConnection.connect();
        String sql = "UPDATE instructors SET name = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newEmail);
            pstmt.setString(3, newPassword);
            pstmt.setInt(4, getId());
            pstmt.executeUpdate();
            System.out.println("Details updated successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to update details: " + e.getMessage());
        }
    }

    private void viewStudentResults() {
        System.out.println("\nView Student Results");
        Connection conn = DatabaseConnection.connect();
        String sql = "SELECT q.id AS quiz_id, q.title AS quiz_title, r.student_id, r.score " +
                "FROM quizzes q " +
                "JOIN results r ON q.id = r.quiz_id " +
                "WHERE q.instructor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Quiz ID: " + rs.getInt("quiz_id") +
                        ", Title: " + rs.getString("quiz_title") +
                        ", Student ID: " + rs.getInt("student_id") +
                        ", Score: " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch results: " + e.getMessage());
        }
    }

    public void addQuiz(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        try {

            String fetchCoursesSql = "SELECT id, name FROM courses WHERE instructor_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchCoursesSql)) {
                pstmt.setInt(1, this.getId());
                ResultSet rs = pstmt.executeQuery();

                System.out.println("Your Courses:");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                }
            }


            System.out.print("Enter the course ID to add a quiz: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter the quiz name: ");
            String quizName = scanner.nextLine();

            String addQuizSql = "INSERT INTO quizzes (name, course_id, instructor_id) VALUES (?, ?, ?)";
            int quizId;
            try (PreparedStatement pstmt = conn.prepareStatement(addQuizSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, quizName);
                pstmt.setInt(2, courseId);
                pstmt.setInt(3, this.getId());
                pstmt.executeUpdate();

                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    quizId = keys.getInt(1);
                } else {
                    System.out.println("Failed to create the quiz.");
                    return;
                }
            }


            while (true) {
                System.out.print("Enter the question text (or type 'done' to finish): ");
                String questionText = scanner.nextLine();
                if (questionText.equalsIgnoreCase("done")) break;

                // Add to the database
                String addQuestionSql = "INSERT INTO questions (quiz_id, question_text,correct_option) VALUES (?, ?, 0)";
                int questionId;
                try (PreparedStatement pstmt = conn.prepareStatement(addQuestionSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, quizId);
                    pstmt.setString(2, questionText);
                    pstmt.executeUpdate();

                    ResultSet keys = pstmt.getGeneratedKeys();
                    if (keys.next()) {
                        questionId = keys.getInt(1);
                    } else {
                        System.out.println("Failed to add the question.");
                        continue;
                    }
                }

                int correctOption = -1;
                for (int i = 1; i <= 4; i++) { // Assuming 4 options
                    System.out.print("Enter option " + i + ": ");
                    String optionText = scanner.nextLine();

                    String addOptionSql = "INSERT INTO options (question_id, option_text) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(addOptionSql)) {
                        pstmt.setInt(1, questionId);
                        pstmt.setString(2, optionText);
                        pstmt.executeUpdate();
                    }

                    if (correctOption == -1) {
                        System.out.print("Is this the correct option? (yes/no): ");
                        if (scanner.nextLine().equalsIgnoreCase("yes")) {
                            correctOption = i;
                        }
                    }
                }

                if (correctOption != -1) {
                    String updateQuestionSql = "UPDATE questions SET correct_option = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuestionSql)) {
                        pstmt.setInt(1, correctOption);
                        pstmt.setInt(2, questionId);
                        pstmt.executeUpdate();
                    }
                } else {
                    System.out.println("No correct option selected. Skipping question.");
                }
            }

            System.out.println("Quiz and questions added successfully!");

        } catch (SQLException e) {
            System.out.println("Error while adding quiz: " + e.getMessage());
        }
    }


    private void manageCourses(Connection conn) {
        System.out.println("\nManage Courses");
        System.out.println("1. Add Course");
        System.out.println("2. Update Course");
        System.out.println("3. Delete Course");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    String sql = "INSERT INTO courses (name, instructor_id) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, courseName);
                        pstmt.setInt(2, this.getId());
                        pstmt.executeUpdate();
                        System.out.println("Course added successfully.");
                    }
                    break;
                case 2:
                    System.out.print("Enter course ID to update: ");
                    int courseId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new course name: ");
                    String updatedName = scanner.nextLine();
                    sql = "UPDATE courses SET name = ? WHERE id = ? AND instructor_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, updatedName);
                        pstmt.setInt(2, courseId);
                        pstmt.setInt(3, this.getId());
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("Course updated successfully.");
                        } else {
                            System.out.println("No course found with the given ID.");
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter course ID to delete: ");
                    courseId = scanner.nextInt();
                    sql = "DELETE FROM courses WHERE id = ? AND instructor_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, courseId);
                        pstmt.setInt(2, this.getId());
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("Course deleted successfully.");
                        } else {
                            System.out.println("No course found with the given ID.");
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Error managing courses: " + e.getMessage());
        }
    }

    private void viewEnrolledStudents(Connection conn) {
        System.out.println("\nView Enrolled Students");
        String sql = "SELECT c.id AS course_id, c.name AS course_name, s.name AS student_name " +
                "FROM courses c " +
                "LEFT JOIN enrollments e ON c.id = e.course_id " +
                "LEFT JOIN students s ON e.student_id = s.id " +
                "WHERE c.instructor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.getId());
            ResultSet rs = pstmt.executeQuery();
            int currentCourse = -1;
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseName = rs.getString("course_name");
                String studentName = rs.getString("student_name");

                if (courseId != currentCourse) {
                    if (currentCourse != -1) {
                        System.out.println();
                    }
                    System.out.println("Course: " + courseName);
                    currentCourse = courseId;
                }

                if (studentName != null) {
                    System.out.println("  - " + studentName);
                } else {
                    System.out.println("  No students enrolled.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing enrolled students: " + e.getMessage());
        }
    }

    private void manageQuizzesAndQuestions(Connection conn) {
        System.out.println("\nManage Quizzes and Questions");
        System.out.println("1. Update Quiz");
        System.out.println("2. Delete Quiz");
        System.out.println("3. Update Question");
        System.out.println("4. Delete Question");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter quiz ID to update: ");
                    int quizId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new quiz name: ");
                    String quizName = scanner.nextLine();
                    String sql = "UPDATE quizzes SET name = ? WHERE id = ? AND instructor_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, quizName);
                        pstmt.setInt(2, quizId);
                        pstmt.setInt(3, this.getId());
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("Quiz updated successfully.");
                        } else {
                            System.out.println("No quiz found with the given ID.");
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter quiz ID to delete: ");
                    quizId = scanner.nextInt();
                    sql = "DELETE FROM quizzes WHERE id = ? AND instructor_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, quizId);
                        pstmt.setInt(2, this.getId());
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("Quiz deleted successfully.");
                        } else {
                            System.out.println("No quiz found with the given ID.");
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter question ID to update: ");
                    int questionId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new question text: ");
                    String questionText = scanner.nextLine();
                    sql = "UPDATE questions SET question_text = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, questionText);
                        pstmt.setInt(2, questionId);
                        pstmt.executeUpdate();
                        System.out.println("Question updated successfully.");
                    }
                    break;
                case 4:
                    System.out.print("Enter question ID to delete: ");
                    questionId = scanner.nextInt();
                    sql = "DELETE FROM questions WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, questionId);
                        pstmt.executeUpdate();
                        System.out.println("Question deleted successfully.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Error managing quizzes and questions: " + e.getMessage());
        }
    }


    private void manageMessages() {
        System.out.println("\nManage Messages");
        // Logic to send and view messages
    }

    private String getEmail(Scanner scanner) {
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

    private String getPassword(Scanner scanner) {
        while (true) {
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (validation.isValidPassword(password)) {
                return password;
            } else {
                System.out.println("Password must meet the required criteria.");
            }
        }
    }
}
