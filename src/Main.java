
import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize database tables (Example)
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS admin (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS instructors (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), approved TINYINT DEFAULT 0)");
            stmt.execute("CREATE TABLE IF NOT EXISTS courses (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), instructor_id INT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS student_courses (student_id INT, course_id INT, PRIMARY KEY(student_id, course_id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, sender_id INT, recipient_id INT, content TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS quizzes (" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "name VARCHAR(255) NOT NULL, " + "course_id INT NOT NULL, " + "instructor_id INT NOT NULL, " + "FOREIGN KEY (course_id) REFERENCES courses(id), " + "FOREIGN KEY (instructor_id) REFERENCES instructors(id)" + ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS questions (" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "quiz_id INT NOT NULL, " + "question_text TEXT NOT NULL, " + "correct_option INT NOT NULL, " + "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" + ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS options (" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "question_id INT NOT NULL, " + "option_text TEXT NOT NULL, " + "FOREIGN KEY (question_id) REFERENCES questions(id)" + ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS results (" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "student_id INT NOT NULL, " + "quiz_id INT NOT NULL, " + "marks INT NOT NULL, " + "FOREIGN KEY (student_id) REFERENCES students(id), " + "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" + ")");
          System.out.println("Database Initialized.");
        } catch (SQLException e) {
            System.out.println("Database Initialization Failed: " + e.getMessage());
        }

        // Example usage
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.printf("  ____        _        ____ _               %n");
            System.out.printf(" | __ ) _   _| |_ ___ / ___| | __ _ ___ ___ %n");
            System.out.printf(" |  _ \\| | | | __/ _ \\ |   | |/ _` / __/ __|%n");
            System.out.printf(" | |_) | |_| | ||  __/ |___| | (_| \\__ \\__ \\%n");
            System.out.printf(" |____/ \\__, |\\__\\___|\\____|_|\\__,_|___/___/%n");
            System.out.printf("        |___/                               %n");
            System.out.println("  Welcome to the Learning Management System");
            System.out.println("---------------------------------------------\n");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit\n");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    loginRegistation.login();
                    break;
                case 2:
                    loginRegistation.register();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

//instructor1@lms.lk
//Instructor@123
