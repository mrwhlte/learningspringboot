package com.jafree.learningspringboot.jafreelearningspringboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner init(ChapterRepository chapterRepository) {
        return args -> {

            chapterRepository.deleteAll().subscribe();
            Flux.just(
                    new Chapter("Chapter1"),
                    new Chapter("Chapter2"),
                    new Chapter("Chapter3")
            ).flatMap(chapterRepository::save)
            .subscribe(System.out::println);
        };
    }
}
