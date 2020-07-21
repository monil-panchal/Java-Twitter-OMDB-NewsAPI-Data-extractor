package com.csci5408.assignment3.service;

import com.csci5408.assignment3.connector.TwitterConnector;
import com.csci5408.assignment3.util.RegexConstant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

import static twitter4j.json.DataObjectFactory.getRawJSON;

/* Twitter business logic class for this application.
 * Rest controller calls methods of this class.
 * This class invokes Twitter search and stream APIs and stores data into MongoDB after filtering.
 */
@Service
public class TwitterService {
    private Logger logger = LoggerFactory.getLogger(TwitterService.class);
    private static final String COLLECTION_TWEETS_RAW = "tweets_raw";
    private static final String COLLECTION_TWEETS_FILTERED = "tweets_filtered";

    @Autowired
    private TwitterConnector twitterConnector;

    @Autowired
    private MongoTemplate mongoTemplate;

    /* Twitter business logic method,
     * invoked by ../twitter/search rest endpoint of this application.
     * This method invokes Twitter's search API using Twitter4j library,
     * and stores the data into MongoDB after filtering.
     */
    public String fetchTwitterDataWithSearch(String keyword) throws TwitterException {

        String res = "{\"result\": \"no result found\"}";

        // Twitter connector configuration
        ConfigurationBuilder configurationBuilder = twitterConnector.buildTwitterConfiguration();
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = twitterFactory.getInstance();

        // Twitter query construction for the given keyword
        Query query = new Query(keyword);
        query.setCount(100);
        QueryResult result = twitter.search(query);

        /* Filtering the "full_text" attribute of the Twitter search api response
         * and removing emojis, URLS and special characters except . and @ .
         * Remaining attributes are kept unchanged for the future use.
         */
        for (Status tweet : result.getTweets()) {
            String json = getRawJSON(tweet);
            logger.info("Raw tweet data" + json);

            //Inserting raw data response in MongoDb
            mongoTemplate.insert(json, COLLECTION_TWEETS_RAW);

            try {
                /* Filtering the "full_text" attribute of the Twitter search api response
                 *  and removing emojis, URLS and special characters except . and @ .
                 */
                JSONObject jsonObject = new JSONObject(json);

                String text = new String(jsonObject.getString("full_text")
                        .replaceAll(RegexConstant.EMOJI_SPECIAL_CHAR_FILTER, "")
                        .replaceAll(RegexConstant.URL_FILTER, "")
                        .replaceAll(RegexConstant.ALPHANUMERIC_FILTER, " ").toString());

                jsonObject.put("full_text", text);

                logger.info("Filtered tweet data to be stored in MongoDB" + jsonObject.toString());
                res = jsonObject.toString();

                //Inserting filtered data response in MongoDb
                mongoTemplate.insert(res, COLLECTION_TWEETS_FILTERED);

            } catch (Exception e) {
                System.out.println("exception e:" + e);
                continue;
            }
        }
        return res;
    }

    /* Twitter business logic method,
     * invoked by ../twitter/stream rest endpoint of this application.
     * This method invokes Twitter's stream API using Twitter4j library to fetch real time tweets,
     * and stores the data into MongoDB after filtering.
     */
    public void fetchTwitterDataWithStream(List<String> keyword) {

        logger.info("Streaming for the keywords:" + keyword);

        // Twitter connector configuration
        ConfigurationBuilder configurationBuilder = twitterConnector.buildTwitterConfiguration();
        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();

        // Twitter query filter construction for the given keyword
        FilterQuery filter = new FilterQuery();
        filter.track(keyword.stream().toArray(String[]::new));

        // Instantiating and creating Twitter asynchronous stream for fetching real time tweets for the given keyword
        StatusListener statusListener = createStreamListener();
        twitterStream.addListener(statusListener);

        twitterStream.filter(filter);

    }

    // TwitterStream listener
    private StatusListener createStreamListener() {
        StatusListener listener = new StatusListener() {

            // Listening to real time tweets.
            @Override
            public void onStatus(Status status) {

                String json = getRawJSON(status);
                logger.info("Raw tweet data" + json);

                //Inserting raw data response in MongoDb
                mongoTemplate.insert(json, COLLECTION_TWEETS_RAW);

                /* Filtering the "text" attribute of the Twitter stream api response
                 *  and removing emojis, URLS and special characters except . and @ .
                 */
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    String text = new String(jsonObject.getString("text")
                            .replaceAll(RegexConstant.EMOJI_SPECIAL_CHAR_FILTER, "")
                            .replaceAll(RegexConstant.URL_FILTER, "")
                            .replaceAll(RegexConstant.ALPHANUMERIC_FILTER, " ").toString());

                    jsonObject.put("text", text);
                    logger.info("Filtered tweet data to be stored in MongoDB" + jsonObject.toString());

                    //Inserting filtered data response in MongoDb
                    mongoTemplate.insert(jsonObject.toString(), COLLECTION_TWEETS_FILTERED);
                } catch (Exception e) {
                    System.out.println("exception e:" + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }
        };
        return listener;
    }
}