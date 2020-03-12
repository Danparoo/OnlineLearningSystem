package Database;
import java.sql.*;

public class examDatabase {

    private static String url = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk/philadelphia";
    private static String username = "philadelphia";
    private static String password = "y8liulbkqy";
    public static Connection connection;

    /**
     * Opens a connection between server and database.
     */

    public static void makeConnection(){
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("makeConnection Success");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection between server and database.
     */

    public static void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static synchronized boolean isAnswerRight(int questionid, String answer){
        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ? AND correctans = ?")){
            statement.setInt(1, questionid);
            statement.setString(2, answer);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static synchronized int countQuestion() throws SQLException {
        int rowCount = 0;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question"))
        {
            ResultSet rs = statement.executeQuery();

            while(rs.next())
            {
                rowCount++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

    public static synchronized void addNewQuestion(Question question){
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO question (questionid,questioncontent,a,b,c,d,correctans) VALUES (?, ?, ?, ?, ?, ?, ?)"))
        {
            statement.setInt(1,  question.getQuestionid());
            statement.setString(2, question.getQuestioncontent());
            statement.setString(3, question.getA());
            statement.setString(4, question.getB());
            statement.setString(5, question.getC());
            statement.setString(6, question.getD());
            statement.setString(7, question.getCorrectans());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
      makeConnection();
      //  System.out.println(isAnswerRight(2,"d"));
       // System.out.println(countQuestion());
       // Question question1=new Question(5, "five plus five equals?", "one", "ten", "eleven", "seven", "b");
	   // addNewQuestion(question1);
        closeConnection();
    }
}


