package twitterFeeds;

class Constants {
    static final String consumerKey = System.getProperty("config.twitter.consumer.key");
    static final String consumerSecret = System.getProperty("config.twitter.consumer.secret");
    static final String accessToken = System.getProperty("config.twitter.access.token");
    static final String accessTokenSecret = System.getProperty("config.twitter.access.secret");
    static final String track = System.getProperty("config.twitter.track");
    static final String sqsAmazonUrl = System.getProperty("config.sqs.url");
}
