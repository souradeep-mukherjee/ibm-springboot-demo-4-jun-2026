package com.ibm.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class IbmSpringbootDemoApplication {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        System.out.println(">>> mongodb.uri      = " + env.getProperty("spring.data.mongodb.uri"));
        System.out.println(">>> mongodb.database = " + env.getProperty("spring.data.mongodb.database"));
        System.out.println(">>> MONGO_URI env    = " + System.getenv("MONGO_URI"));
    }

    public static void main(String[] args) {
        SpringApplication.run(IbmSpringbootDemoApplication.class, args);
    }
}

//package com.ibm.demo;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class IbmSpringbootDemoApplication {
//
////	@Value("${spring.data.mongodb.uri}")
////	private String mongoUri;
////
////	@PostConstruct
////	public void init() {
////		System.out.println(">>> Mongo URI = " + mongoUri);
////	}
//
//	public static void main(String[] args) {
//		SpringApplication.run(IbmSpringbootDemoApplication.class, args);
//	}
//}