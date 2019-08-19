package utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import twitterFeeds.ExtractedLink;

public class DataStorage {

	// Dev Mode
	//public static final String hostname = "twitter-feeds.cluster-cipuswkdggje.us-east-1.rds.amazonaws.com";
	//public static final String db_database = "twitter_links";
	//public static final String username = "cloud";
	//public static final String password = "------";
	
	// Prod Mode
	public static final String hostname = System.getProperty("config.mysql.hostname");
	public static final String db_database = System.getProperty("config.mysql.database");
	public static final String username = System.getProperty("config.mysql.username");
	public static final String password = System.getProperty("config.mysql.password");
	
	Connection conn;

	  public DataStorage() throws SQLException {
			try {
				//Class.forName("com.mysql.jdbc.Driver");
			
				String url = "jdbc:mysql://" + hostname + ":3306/" + db_database + "?user=" + username + "&password=" + password;
			    conn = DriverManager.getConnection(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }

	  /**
	   * Add link to the database
	   */
	  public void addLink(ExtractedLink link, String track) {
      PreparedStatement st;
		try {
			
			java.util.Date date = Calendar.getInstance().getTime();  
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            String strDate = dateFormat.format(date); 
						
		    String query = "INSERT INTO TWITTER_LINKS (LINK, TRACK, CURR_DATE, CONTENT, TITLE, DESCRIPTION, SCREENSHOT_URL) " + 
		     "VALUES ('" + link.getUrl() + "', '" + track + "', '" + strDate + "', '" + link.getContent() + "', '" + link.getTitle() + "', '" + link.getDescription() + "', '" + link.getScreenshotURL() + "')";
		    
		    st = conn.prepareStatement(query);
		    
		    // note that i'm leaving "date_created" out of this insert statement
		    st.executeUpdate(query);

		      conn.close();
		      
		      deleteOldLinks();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  private void deleteOldLinks() {
		  // Delete all except of last 1000 feeds
		  String query = "DELETE FROM TWITTER_LINKS WHERE CURR_DATE NOT IN (SELECT CURR_DATE FROM (SELECT CURR_DATE FROM TWITTER_LINKS ORDER BY CURR_DATE DESC LIMIT 1000) foo)";
		  
		  PreparedStatement st;
		try {
			st = conn.prepareStatement(query);
			
			  st.execute(query);
			  
			  conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

	  /**
	   * Search for a link
	   * @param query The query to search
	   */
	  public List<ExtractedLink> search(String query) {
	    /*
	    Search for query in the database and return the results
	     */

	    return null;
	  }
}
