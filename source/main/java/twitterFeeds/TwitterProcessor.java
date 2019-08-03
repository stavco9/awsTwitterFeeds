package twitterFeeds;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public class TwitterProcessor {
    public static void main(String[] args) {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        List<Message> sqsMessages = sqs.receiveMessage(Constants.sqsAmazonUrl).getMessages();

        for (Message message : sqsMessages) {
            System.out.println(message.toString());
        }
    }
}
