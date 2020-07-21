package com.csci5408.assignment3.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import twitter4j.conf.ConfigurationBuilder;

/* Twitter connection configuration builder class.
 * This is a singleton class,
 * instance will be created only once and re-used whenever accessing Twitter api.
 */
@Component
public class TwitterConnector {

    @Value("${data.twitter.consumer.key}")
    private String consumerKey;

    @Value("${data.twitter.consumer.secret}")
    private String consumerSecret;

    @Value("${data.twitter.access.token}")
    private String accessToken;

    @Value("${data.twitter.token.secret}")
    private String tokenSecret;

    public ConfigurationBuilder buildTwitterConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setJSONStoreEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(tokenSecret);

        return configurationBuilder;

    }


}
