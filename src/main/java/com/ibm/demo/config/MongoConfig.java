package com.ibm.demo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

	@Bean
	public MongoDatabaseFactory mongoDatabaseFactory() {
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		return new SimpleMongoClientDatabaseFactory(mongoClient, "ibm-ems");
	}
}