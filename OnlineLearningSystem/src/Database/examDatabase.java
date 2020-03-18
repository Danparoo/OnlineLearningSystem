package Database;
import java.sql.*;
import java.util.ArrayList;

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
                "INSERT INTO question (questionid,questioncontent,a,b,c,d,correctans,topicname) VALUES (?,?, ?, ?, ?, ?, ?, ?)"))
        {
            statement.setInt(1,  question.getQuestionid());
            statement.setString(2, question.getQuestioncontent());
            statement.setString(3, question.getA());
            statement.setString(4, question.getB());
            statement.setString(5, question.getC());
            statement.setString(6, question.getD());
            statement.setString(7, question.getCorrectans());
            statement.setString(8, question.getTopicname());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
     public static synchronized Question getQuestion (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String questioncontent= rs.getString("questioncontent");
            String optionA= rs.getString("a");
            String optionB= rs.getString("b");
            String optionC= rs.getString("c");
            String optionD= rs.getString("d");
            String answer= rs.getString("correctans");
            String topicname= rs.getString("topicname");
            Question question1 = new Question(questionid, questioncontent, optionA, optionB, optionC, optionD, answer, topicname);

            return question1;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static synchronized ArrayList<Question> getQuestions (String topicName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE topicname = ?"))
        {
            statement.setString(1, topicName);
            ResultSet rs = statement.executeQuery();
            ArrayList<Question> questions = new ArrayList<>();

            while(rs.next()) {
                int questionid= rs.getInt("questionid");
                String questioncontent= rs.getString("questioncontent");
                String optionA= rs.getString("a");
                String optionB= rs.getString("b");
                String optionC= rs.getString("c");
                String optionD= rs.getString("d");
                String answer= rs.getString("correctans");
                String topicname= rs.getString("topicname");
                questions.add(new Question(questionid, questioncontent, optionA, optionB, optionC, optionD, answer, topicname));
            }
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static synchronized String getQuestionContent (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
                String questioncontent= rs.getString("questioncontent");
            return questioncontent;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static synchronized String getOptionA (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String optionA= rs.getString("a");
            return optionA;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static synchronized String getOptionB (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String optionB= rs.getString("b");
            return optionB;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static synchronized String getOptionC (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String optionC= rs.getString("c");
            return optionC;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static synchronized String getOptionD (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String optionD= rs.getString("d");
            return optionD;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static synchronized String getAnswer (int questionid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM question WHERE questionid = ?"))
        {
            statement.setInt(1, questionid);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String answer= rs.getString("correctans");
            return answer;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
       makeConnection();
      //  System.out.println(getOptionA (2));
      //  System.out.println(getOptionB (3));
       // System.out.println(getOptionC (4));
      //  System.out.println(getOptionD (5));
       // ArrayList<Question> questions = getQuestions ("arithmetic");
      //  for(int i = 0; i<questions.size();i++)
      //  System.out.println(questions.get(i).getQuestioncontent() + "\n");

      //  System.out.println(isAnswerRight(2,"d"));
       // System.out.println(countQuestion());
       // Question question1=new Question(5, "five plus five equals?", "one", "ten", "eleven", "seven", "b");
	   // addNewQuestion(question1);
        closeConnection();
    }
}


