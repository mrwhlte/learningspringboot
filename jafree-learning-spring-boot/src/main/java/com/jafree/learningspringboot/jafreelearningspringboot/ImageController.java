package com.jafree.learningspringboot.jafreelearningspringboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ImageController {

    private final String API_BASE_PATH = "api";

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(API_BASE_PATH + "/images")
    public Flux images() {
        return Flux.just(
                new Image("1", "image 1"),
                new Image("2", "image 2"),
                new Image("3", "image 3")
        );
    }

//    @GetMapping("/")
//    public Flux index() {
//        return imageService.findAllImages();
//    }
}
