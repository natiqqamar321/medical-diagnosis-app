package com.example.diagnosis;


import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoUriChecker {
  @Value("${spring.data.mongodb.uri}")
  private String uri;

  @PostConstruct
  public void checkUri() {
    System.out.println("Mongo URI at startup: " + uri);
  }
}
