import java.sql.*;
import java.util.Scanner;

public class quiz {
    public void addQuiz(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Step 1: Display courses for the instructor
            String fetchCoursesSql = "SELECT id, name FROM courses WHERE instructor_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fetchCoursesSql)) {
                pstmt.setInt(1, Instructor.getId()); // Use instructor's ID
                ResultSet rs = pstmt.executeQuery();

                System.out.println("Your Courses:");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + ". " + rs.getString("name"));
                }
            }

            // Step 2: Select a course
            System.out.print("Enter the course ID to add a quiz: ");
            int courseId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Step 3: Add a quiz
            System.out.print("Enter the quiz name: ");
            String quizName = scanner.nextLine();

            String addQuizSql = "INSERT INTO quizzes (name, course_id, instructor_id) VALUES (?, ?, ?)";
            int quizId;
            try (PreparedStatement pstmt = conn.prepareStatement(addQuizSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, quizName);
                pstmt.setInt(2, courseId);
                pstmt.setInt(3, Instructor.getId()); // Use instructor's ID
                pstmt.executeUpdate();

                // Retrieve the generated quiz ID
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    quizId = keys.getInt(1);
                } else {
                    System.out.println("Failed to create the quiz.");
                    return;
                }
            }

            // Step 4: Add questions in a loop
            while (true) {
                System.out.print("Enter the question text (or type 'done' to finish): ");
                String questionText = scanner.nextLine();
                if (questionText.equalsIgnoreCase("done")) break;

                // Add question to the database
                String addQuestionSql = "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)";
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

                // Update the correct option for the question
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
}
