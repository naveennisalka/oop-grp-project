import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    protected static int id;
    protected static String name;
    protected static String email;
    protected static String password;

    public User(int id, String name, String email, String password) {
        User.id = id;
        User.name = name;
        User.email = email;
        User.password = password;
    }

    protected static int getId() {
        return id;
    }
    protected static String getName() {
        return name;
    }
    protected static String getEmail() {
        return email;
    }
    protected static String getPassword() {
        return password;
    }

    public void sendMessage(int recipientId, String message) {
        Connection conn = DatabaseConnection.connect();
        String sql = "INSERT INTO messages(sender_id, recipient_id, content) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, recipientId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            System.out.println("Message Sent Successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to Send Message: " + e.getMessage());
        }
    }

    public void viewMessages() {
        Connection conn = DatabaseConnection.connect();
        String sql = "SELECT sender_id, content FROM messages WHERE recipient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Messages:");
            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                String content = rs.getString("content");
                System.out.println("From User " + senderId + ": " + content);
            }
        } catch (SQLException e) {
            System.out.println("Failed to Retrieve Messages: " + e.getMessage());
        }
    }



}