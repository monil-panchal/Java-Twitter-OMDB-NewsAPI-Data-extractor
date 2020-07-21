package com.csci5408.assignment3.data.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/* MongoDB configuration and connection builder.
 * This is a singleton class,
 * instance will be created only once and re-used whenever accessing MongoDB query/connection.
 */
@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    private MongoClientURI uri =
            new MongoClientURI("mongodb+srv://MonilMongo:MonilMongo@cluster0-kyao3.mongodb.net/test?retryWrites=true&w=majority");
    private MongoClient mongoClient = new MongoClient(uri);
    private MongoDatabase database = mongoClient.getDatabase("data-assignment-3");

    @Override
    protected String getDatabaseName() {
        return database.getName();
    }

    @Override
    public MongoClient mongoClient() {
        return mongoClient;
    }

}