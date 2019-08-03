package twitterFeeds;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

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

public class TwitterStreamAPI {

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
		MetricsProcessor metricsProcessor = new MetricsProcessor();

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
						HashMap<String, String> twitterObjToSQS = new HashMap<>();
	
						twitterObjToSQS.put("link", entity.getExpandedURL());
						twitterObjToSQS.put("track", Constants.track);
					
						HashMap<String, String> BodyRequest = new HashMap<>();
	
						BodyRequest.put("Action", "SendMessage");
						BodyRequest.put("MessageBody", twitterObjToSQS.toString());
						
						String bodyRequestString = getDataString(BodyRequest);
						
						URL urlToAws = new URL(Constants.sqsAmazonUrl);
							
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
								  new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
								    StringBuilder response = new StringBuilder();
								    String responseLine;
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

				metricsProcessor.collectCollectedUrlsMetric(status.getURLEntities().length, Constants.track);
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
	        .setOAuthConsumerKey(Constants.consumerKey)
	        .setOAuthConsumerSecret(Constants.consumerSecret)
	        .setOAuthAccessToken(Constants.accessToken)
	        .setOAuthAccessTokenSecret(Constants.accessTokenSecret);

	    // Create our Twitter stream
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    
	    twitterStream.addListener(listener);
	    
	    FilterQuery twitterFilterQuery = new FilterQuery();
	    twitterFilterQuery.track(Constants.track);
	    twitterFilterQuery.language("en");
	    
	    twitterStream.filter(twitterFilterQuery);
	}

}
