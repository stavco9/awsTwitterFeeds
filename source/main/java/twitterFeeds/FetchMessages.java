package twitterFeeds;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import utils.DataStorage;
import utils.LinkExtractor;

import java.sql.SQLException;
import java.util.*;

public class FetchMessages {
	
	// Dev Mode
	//private static final String sqsAmazonUrl = "https://sqs.us-east-1.amazonaws.com/035154003643/colmanTwitterQueue";
	
	// Prod Mode
	private static final String sqsAmazonUrl = System.getProperty("config.sqs.url");
	
	private static AmazonSQS client = AmazonSQSClientBuilder.defaultClient();
	
	private static HashMap<String, String> convertStringToHashMap(String str){
		str = str.substring(1, str.length()-1);           //remove curly brackets
		String[] keyValuePairs = str.split(",");              //split the string to creat key-value pairs
		HashMap<String,String> map = new HashMap<>();               

		for(String pair : keyValuePairs)                        //iterate over the pairs
		{
		    String[] entry = pair.split("=");                   //split the pairs to get key and value 
		    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
		}
		
		return map;
	}
	
	public static void ListenToSQS() {
		
		// Receive a message from the SQS
		while (true) {
			ReceiveMessageResult result =
			client.receiveMessage(sqsAmazonUrl);
			List<Message> messages = result.getMessages();
			if (messages.size() == 0) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				for (Message message : messages) {
					String MessageStr = message.getBody();
					
					HashMap<String, String> MessageMap = convertStringToHashMap(MessageStr);
					
					ExtractedLink link = LinkExtractor.extractContent(MessageMap.get("link"));
					
					DataStorage storage;
					
					try {
						storage = new DataStorage();
					
						storage.addLink(link, MessageMap.get("track"));
						
						System.out.println(MessageMap.get("link"));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ListenToSQS();
	}
}
