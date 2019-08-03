package twitterFeeds;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamAPIOld {
	
	
	// Prod Mode
	private static final String consumerKey = System.getProperty("config.twitter.consumer.key");
	private static final String consumerSecret = System.getProperty("config.twitter.consumer.secret");
	private static final String accessToken = System.getProperty("config.twitter.access.token");
	private static final String accessTokenSecret = System.getProperty("config.twitter.access.secret");
	private static final String track = System.getProperty("config.twitter.track");
	private static final String sqsAmazonUrl = System.getProperty("config.sqs.url");
	
	private static String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for(Map.Entry<String, String> entry : params.entrySet()){
	        if (first)
	            first = false;
	        else
	            result.append("&");    
	        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }    
	    return result.toString();
	}
	
	public static void main(String[] args) {
		StatusListener listener = new StatusListener() {
			
			@Override
			public void onException(Exception ex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStatus(Status status) {
                
				for(URLEntity entity: status.getURLEntities()) {
					try {
						HashMap<String, String> twitterObjToSQS = new HashMap<String, String>();
	
						twitterObjToSQS.put("link", entity.getExpandedURL());
						twitterObjToSQS.put("track", track);

						HashMap<String, String> BodyRequest = new HashMap<String, String>(); 
	
						BodyRequest.put("Action", "SendMessage");
						BodyRequest.put("MessageBody", twitterObjToSQS.toString());
						
						String bodyRequestString = getDataString(BodyRequest);
						
						URL urlToAws = new URL(sqsAmazonUrl);
							
						HttpURLConnection conn = (HttpURLConnection) urlToAws.openConnection();
						
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						conn.setRequestProperty("content-length", Integer.toString(bodyRequestString.length()));
						conn.setDoOutput(true);
						
						byte[] postData = bodyRequestString.getBytes( StandardCharsets.UTF_8 );
						
						DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
						
						stream.write(postData);
						
						stream.flush();
						
						try(BufferedReader br = new BufferedReader(
								  new InputStreamReader(conn.getInputStream(), "utf-8"))) {
								    StringBuilder response = new StringBuilder();
								    String responseLine = null;
								    while ((responseLine = br.readLine()) != null) {
								        response.append(responseLine.trim());
						   }
						    System.out.println(response.toString());
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			@Override
			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// TODO Auto-generated method stub
				
			}
		};
		
	    // Create our configuration
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	        .setOAuthConsumerKey(consumerKey)
	        .setOAuthConsumerSecret(consumerSecret)
	        .setOAuthAccessToken(accessToken)
	        .setOAuthAccessTokenSecret(accessTokenSecret);

	    // Create our Twitter stream
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    
	    twitterStream.addListener(listener);
	    
	    FilterQuery twitterFilterQuery = new FilterQuery();
	    twitterFilterQuery.track(track);
	    twitterFilterQuery.language("en");
	    
	    twitterStream.filter(twitterFilterQuery);
	}
}
