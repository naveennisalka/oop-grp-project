import java.sql.*;
import java.util.Scanner;

public class Student extends User {
    private Scanner scanner;

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password);
        this.scanner = new Scanner(System.in);
    }

    public void menu(Connection conn) {
        while (true) {
            System.out.println("\nStudent Menu");
            System.out.println("\n---------------------------------------------");
            System.out.println("-------------     Student Menu     -------------   ");
            System.out.println("---------------------------------------------\n");
            System.out.println("1. Edit My Details");
            System.out.println("2. View My Results");
            System.out.println("3. Answer Quizzes");
            System.out.println("4. Send/View Messages");
            System.out.println("5. Courses");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    editDetails(conn);
                    break;
                case 2:
                    viewResults(conn);
                    break;
                case 3:
                    answerQuizzes(conn);
                    break;
                case 4:
                    manageMessages(conn);
                    break;
                case 5:
                    courses(conn);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void editDetails(Connection conn) {
        System.out.println("\nEdit My Details");
        System.out.print("Enter new name (or press Enter to skip): ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email (or press Enter to skip): ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new password (or press Enter to skip): ");
        String newPassword = scanner.nextLine();

        try {
            String updateSql = "UPDATE students SET name = ?, email = ?, password = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, newName.isEmpty() ? this.getName() : newName);
                pstmt.setString(2, newEmail.isEmpty() ? this.getEmail() : newEmail);
                pstmt.setString(3, newPassword.isEmpty() ? this.getPassword() : newPassword);
                pstmt.setInt(4, this.getId());
                pstmt.executeUpdate();
                System.out.println("Details updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating details: " + e.getMessage());
        }
    }

    private void viewResults(Connection conn) {
        System.out.println("\nView My Results");
        try {
            String resultsSql = "SELECT q.name AS quiz_name, r.marks " +
                    "FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.id " +
                    "WHERE r.student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(resultsSql)) {
                pstmt.setInt(1, this.getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    System.out.println("Quiz: " + rs.getString("quiz_name") + ", Marks: " + rs.getInt("marks"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving results: " + e.getMessage());
        }
    }

    private void answerQuizzes(Connection conn) {
        System.out.println("\nAnswer Quizzes");
        try {

            String fetchQuizzesSql = "SELECT DISTINCT q.id AS quiz_id, q.name AS quiz_name, c.name AS course_name " +
                    "FROM quizzes q " +
                    "JOIN courses c ON q.course_id = c.id " +
                    "JOIN student_courses sc ON sc.course_id = c.id " +
                    "WHERE sc.student_id = ? AND q.id NOT IN " +
                    "(SELECT quiz_id FROM results WHERE student_id = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchQuizzesSql)) {
                pstmt.setInt(1, this.getId());
                pstmt.setInt(2, this.getId());
                ResultSet rs = pstmt.executeQuery();

                System.out.println("Available Quizzes:");
                while (rs.next()) {
                    System.out.println("Quiz ID: " + rs.getInt("quiz_id") +
                            ", Quiz Name: " + rs.getString("quiz_name") +
                            ", Course: " + rs.getString("course_name"));
                }
            }


            System.out.print("Enter the Quiz ID to answer (or 0 to cancel): ");
            int quizId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (quizId == 0) return;

            String fetchQuestionsSql = "SELECT id, question_text FROM questions WHERE quiz_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchQuestionsSql)) {
                pstmt.setInt(1, quizId);
                ResultSet rs = pstmt.executeQuery();

                int totalQuestions = 0;
                int correctAnswers = 0;

                while (rs.next()) {
                    int questionId = rs.getInt("id");
                    String questionText = rs.getString("question_text");

                    System.out.println("Question: " + questionText);


                    String fetchOptionsSql = "SELECT id, option_text FROM options WHERE question_id = ?";
                    try (PreparedStatement optPstmt = conn.prepareStatement(fetchOptionsSql)) {
                        optPstmt.setInt(1, questionId);
                        ResultSet optRs = optPstmt.executeQuery();
                        while (optRs.next()) {
                            System.out.println(optRs.getInt("id") + ". " + optRs.getString("option_text"));
                        }
                    }

                    System.out.print("Your answer (option ID): ");
                    int answerId = scanner.nextInt();
                    scanner.nextLine();


                    String checkAnswerSql = "SELECT id FROM options WHERE question_id = ? AND id = " +
                            "(SELECT correct_option FROM questions WHERE id = ?)";
                    try (PreparedStatement ansPstmt = conn.prepareStatement(checkAnswerSql)) {
                        ansPstmt.setInt(1, questionId);
                        ansPstmt.setInt(2, questionId);
                        ResultSet ansRs = ansPstmt.executeQuery();
                        if (ansRs.next() && ansRs.getInt("id") == answerId) {
                            correctAnswers++;
                        }
                    }
                    totalQuestions++;
                }


                int marks = (correctAnswers * 100) / totalQuestions;
                String insertResultsSql = "INSERT INTO results (student_id, quiz_id, marks) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt1 = conn.prepareStatement(insertResultsSql)) {
                    pstmt1.setInt(1, this.getId());
                    pstmt1.setInt(2, quizId);
                    pstmt1.setInt(3, marks);
                    pstmt1.executeUpdate();
                }
                System.out.println("Quiz completed! You scored: " + marks + "%");
            }
        } catch (SQLException e) {
            System.out.println("Error answering quizzes: " + e.getMessage());
        }
    }

    private void courses(Connection conn) {
        System.out.println("\nManage Courses");

        try {
            // Show all courses
            System.out.println("\nAll Courses:");
            String fetchAllCoursesSql = "SELECT id, name FROM courses";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(fetchAllCoursesSql)) {
                while (rs.next()) {
                    System.out.println("Course ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                }
            }

            // Show enrolled courses
            System.out.println("\nEnrolled Courses:");
            String fetchEnrolledCoursesSql = "SELECT c.id, c.name FROM student_courses sc " +
                    "JOIN courses c ON sc.course_id = c.id WHERE sc.student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchEnrolledCoursesSql)) {
                pstmt.setInt(1, this.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Course ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                    }
                }
            }

            //unenrolled courses
            System.out.println("\nUnenrolled Courses:");
            String fetchUnenrolledCoursesSql = "SELECT id, name FROM courses " +
                    "WHERE id NOT IN (SELECT course_id FROM student_courses WHERE student_id = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchUnenrolledCoursesSql)) {
                pstmt.setInt(1, this.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Course ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                    }
                }
            }


            System.out.println("\n1. Enroll in a Course");
            System.out.println("2. Unenroll from a Course");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    enrollInCourse(conn);
                    break;
                case 2:
                    unenrollFromCourse(conn);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Returning to main menu.");
            }

        } catch (SQLException e) {
            System.out.println("Error managing courses: " + e.getMessage());
        }
    }

    private void enrollInCourse(Connection conn) {
        System.out.println("\nEnroll in a Course");
        System.out.print("Enter the Course ID to enroll: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();
        try {
            String enrollSql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(enrollSql)) {
                pstmt.setInt(1, this.getId());
                pstmt.setInt(2, courseId);
                pstmt.executeUpdate();
                System.out.println("Successfully enrolled in course ID: " + courseId);
            }
        } catch (SQLException e) {
            System.out.println("Error enrolling in course: " + e.getMessage());
        }
    }

    private void unenrollFromCourse(Connection conn) {
        System.out.println("\nUnenroll from a Course");
        System.out.print("Enter the Course ID to unenroll: ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String unenrollSql = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(unenrollSql)) {
                pstmt.setInt(1, this.getId());
                pstmt.setInt(2, courseId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Successfully unenrolled from course ID: " + courseId);
                } else {
                    System.out.println("You are not enrolled in this course.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error unenrolling from course: " + e.getMessage());
        }
    }

    private void manageMessages(Connection conn) {
        System.out.println("\nSend/View Messages");


        try {
            String fetchMessagesSql = "SELECT m.content, u.name AS sender_name " +
                    "FROM messages m " +
                    "JOIN users u ON m.sender_id = u.id " +
                    "WHERE m.recipient_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchMessagesSql)) {
                pstmt.setInt(1, this.getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    System.out.println("From: " + rs.getString("sender_name") + " - " + rs.getString("content"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }


        System.out.print("Enter the recipient ID to send a message (or 0 to cancel): ");
        int recipientId = scanner.nextInt();
        scanner.nextLine();
        if (recipientId == 0) return;

        System.out.print("Enter your message: ");
        String content = scanner.nextLine();

        try {
            String sendMessageSql = "INSERT INTO messages (sender_id, recipient_id, content) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sendMessageSql)) {
                pstmt.setInt(1, this.getId());
                pstmt.setInt(2, recipientId);
                pstmt.setString(3, content);
                pstmt.executeUpdate();
                System.out.println("Message sent!");
            }
        } catch (SQLException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
