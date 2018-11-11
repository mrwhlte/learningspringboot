package com.jafree.learningspringboot.jafreelearningspringboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase {

    @Bean
    CommandLineRunner init(MongoOperations mongoOperations) {
        return args -> {

            mongoOperations.dropCollection(Image.class);

            mongoOperations.insert(new Image("1",
                    "first.jpg"));
            mongoOperations.insert(new Image("2",
                    "second.png"));
            mongoOperations.insert(new Image("3",
                    "third.jpg"));

            mongoOperations.findAll(Image.class).forEach(image -> {
                System.out.println(image.toString());
            });

        };
    }
}

