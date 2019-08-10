package twitterFeeds;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

import java.util.HashMap;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class TwitterStreamAPI {
	
	private static AmazonSQS client = AmazonSQSClientBuilder.defaultClient();
	
	// Prod Mode
	private static final String consumerKey = System.getProperty("config.twitter.consumer.key");
	private static final String consumerSecret = System.getProperty("config.twitter.consumer.secret");
	private static final String accessToken = System.getProperty("config.twitter.access.token");
	private static final String accessTokenSecret = System.getProperty("config.twitter.access.secret");
	private static final String track = System.getProperty("config.twitter.track");
	private static final String sqsAmazonUrl = System.getProperty("config.sqs.url");
	
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
						
						SendMessageRequest send_msg_request = new SendMessageRequest()
						        .withQueueUrl(sqsAmazonUrl)
						        .withMessageBody(twitterObjToSQS.toString())
						        .withDelaySeconds(5);
						
						client.sendMessage(send_msg_request);
						
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
