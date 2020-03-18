package Database;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
public class Database {

	
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
	     
	     /**
	      * Checks if the input usernames and password combination is contained
	      * in the database.
	      * @param chatname of the current client which is String.
	      * @param password of the input password which is String.
	      * @return True if valid user, else False.
	      */
	     public static synchronized boolean isValidUser(String username, String password){
	         try (PreparedStatement statement = connection.prepareStatement(
	         		"SELECT * FROM users WHERE username = ? AND password = ?")){
	             statement.setString(1, username);
	             statement.setString(2, password);
	             ResultSet rs = statement.executeQuery();
	             if (rs.next())
	                 return true;
	             else {
	             	return false;
	             }
	         } catch (SQLException e) {
	             e.printStackTrace();
	             return false;
	         }
	     }
	     
	     /**
	      * Checks if the input username exists in the database.
	      * @param username Username supplied by the current client.
	      * @return True if the username exists, else False.
	      */
	     public static synchronized boolean isUserExisted(String username){
	         try(PreparedStatement statement = connection.prepareStatement(
	         		"SELECT * FROM users WHERE username = ?")){
	             statement.setString(1, username);
	             ResultSet rs = statement.executeQuery();
	             return rs.next();
	         } catch (SQLException e) {
	             e.printStackTrace();
	             return false;
	         }
	     }
	     
	     /**
	      * Inserts a new user into the database.
	      * @param username Username of the new user.
	      * @param password the new user.
	      */
	     public static synchronized void addUser(String username, String password){
	         try (PreparedStatement statement = connection.prepareStatement(
	         		"INSERT INTO users (username, password) VALUES (?, ?)")){
	             statement.setString(1, username);
	             statement.setString(2, password);
	             statement.executeUpdate();
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	     }
	     
	     /**
	      * Inserts a new user data into the database.
	      * @param username Username of the new user.
	      * @param salt Salt value for the new user.
	      * @param pwHash Hash of password for the new user.
	      */
	     public static synchronized void insertNewUser(String username, String realname,String password,String usertype,String sex,
	    		 String email,String profilephoto,String school){
	         try (PreparedStatement statement = connection.prepareStatement(
	         		"INSERT INTO users (username, realname,password,usertype,sex,email,profilephoto,school) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")){
	             statement.setString(1, username);
	             statement.setString(2, realname);
	             statement.setString(3, password);
	             statement.setString(4, usertype);
	             statement.setString(5, sex);
	             statement.setString(6, email);
	             statement.setString(7, profilephoto);
	             statement.setString(8, school);
	             statement.executeUpdate();
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	     }
	     /**
	      * Inserts a message into the database message history table.
	      * @param message Message object containing fromuserid, touserid, postcontent, and sendtime.
	      */
	     //���ϵͳ���ṩsendtime���ݿ�Ҳ�����Զ�����
	      public static synchronized void insertMessage(Messages message) {
		     	try (PreparedStatement statement = connection.prepareStatement(
		     			"INSERT INTO messages (fromuser,touser,postcontent,sendtime) VALUES (?, ?, ?, ?)"))
		     	{
		     		statement.setString(1,  message.getFromuser());
		     		statement.setString(2, message.getTouser());
		     		statement.setString(3, message.getPostcontent());
		     		statement.setTimestamp(4, message.getSendtime());
		     		statement.executeUpdate();
		     	} catch (SQLException e) {
		     		e.printStackTrace();
		     	}
		     }
	     
	     
	     /**
	      * Gets the message history for a particular chat.
	      * @param chatname Name of the chat being queried.
	      * @return ArrayList of Message objects for the specified chat.
	      */	     
	     public static synchronized ArrayList<Messages> retrieveMessages (String fromuser,String touser) {
		     	try (PreparedStatement statement = connection.prepareStatement(
		     			"SELECT * FROM messages WHERE fromuser = ?AND touser = ?ORDER BY sendtime DESC"))
		     	{
		     		statement.setString(1, fromuser);
		     		statement.setString(2, touser);
		     		ResultSet rs = statement.executeQuery();
		     		ArrayList<Messages> messages = new ArrayList<>();
		     		
		     		while(rs.next()) {
		     			String fromUser= rs.getString("fromuser");
		     			String toUser= rs.getString("touser");
		     			String msg = rs.getString("postcontent");
		     			Timestamp time = rs.getTimestamp("sendtime");
		     			messages.add(new Messages(fromUser,toUser, msg, time));
		     		}
		     		return messages;
		     	} catch (SQLException e) {
		     		e.printStackTrace();
		     		return null;
		     	}
		     }   
	     
//	     public static synchronized TreeSet<Messages> retrieveMessages2 (int fromuserid) {
//		     	try (PreparedStatement statement = connection.prepareStatement(
//		     			"SELECT * FROM messages WHERE fromuserid = ?"))
//		     	{
//		     		statement.setInt(1, fromuserid);
//		     		ResultSet rs = statement.executeQuery();
//		     		TreeSet<Messages> messages = new TreeSet<>();
//		     		
//		     		while(rs.next()) {
//		     			int fromUser= rs.getInt(fromuserid);
//		     			int toUser= rs.getInt(fromuserid);
//		     			String msg = rs.getString("postcontent");
//		     			Timestamp time = rs.getTimestamp("sendtime");
//		     			messages.add(new Messages(fromUser,toUser, msg, time));
//		     		}
//		     		return messages;
//		     	} catch (SQLException e) {
//		     		e.printStackTrace();
//		     		return null;
//		     	}
//		     }   
	     /**
	      * Checks if the input groupname exists in the database.
	      * @param groupname supplied by the current client.
	      * @return True if the groupname exists, else False.
	      */  
	     public static synchronized boolean groupExists(String groupname){
	 		try (PreparedStatement statement = connection.prepareStatement(
	 				"SELECT * FROM usergroups WHERE groupname = ?")) {
	 			statement.setString(1, groupname);
	 			return statement.executeQuery().next();
	 		} catch (SQLException e) {
	 			e.printStackTrace();
	 			return false;
	 		}
	 	}   
	     
	     /**
	      * Inserts a group into the database usergroups table.
	      * @param group object containing groupname, adminid, groupintro.
	      */ 
	     public static synchronized void insertNewGroup(String groupname,int adminid,String groupintro){
	         try (PreparedStatement statement = connection.prepareStatement(
	         		"INSERT INTO usergroups (groupname, adminid, groupintro) VALUES (?, ?, ?)")){
	             statement.setString(1, groupname);
	             statement.setInt(2, adminid);
	             statement.setString(3, groupintro);
	             statement.executeUpdate();
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	     }
	     
	     /**
	 	 * Adds a userid to a specified chat in the database.
	 	 * @param username Name of chat to add user into.
	 	 * @param userid of user.
	 	 */
	 	public static synchronized void addUserToGroup(String groupname, int userid){
	 	    try (PreparedStatement statement = connection.prepareStatement(
	 	    		"INSERT INTO usergroups (groupname, userid) VALUES (?, ?)"))
	 	    {
	 	        statement.setString(1, groupname);
	 	        statement.setInt(2, userid);
	 	        statement.executeUpdate();
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	     }

	 	/**
	 	 * Removes a userid from a specified chat in the database.
	 	 * @param groupname Name of chat to remove user from.
	 	 * @param userid of user.
	 	 */
	     public static synchronized void removeUserFromGroup(String groupname, int userid){
	 	    try (PreparedStatement statement = connection.prepareStatement(
	 	    		"DELETE FROM usergroups WHERE groupname = ? AND userid = ?"))
	 	    {
	 	        statement.setString(1, groupname);
	 	        statement.setInt(2, userid);
	 	        statement.executeUpdate();
	         } catch (SQLException e) {
	 	        e.printStackTrace();
	         }
	     }
	     
	  public static void main(String[] args) {
	  makeConnection();
	  System.out.println(isValidUser("Luffy","Luffy123"));
	  System.out.println(isUserExisted("zoro"));
	  System.out.println(isUserExisted("Sanji"));
//	  insertNewUser("zoro", null, "kuyina", "student", null, null, null, null);
//	  Messages message1=new Messages(1, 4, "Hi,welcome", null);
//	  insertMessage(message1);
	  retrieveMessages1(1);
//	  retrieveMessages2(1);
	  System.out.println(retrieveMessages1(1).toString());
//	  System.out.println(retrieveMessages2(1).toString());
	  System.out.println(groupExists("boys"));
//	  insertNewGroup("fighting",3,"3people");
	  addUserToGroup("fighting",5);
	  addUserToGroup("fighting",1);
	  addUserToGroup("fighting",2);
	  removeUserFromGroup("fighting",2);
	  removeUserFromGroup("fighting",1);
	  closeConnection();
	  }
	
}
