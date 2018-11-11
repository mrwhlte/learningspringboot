package com.jafree.learningspringboot.jafreelearningspringboot;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
public class HomeController {

//    @GetMapping
//    public String greeting1(@RequestParam(required = false, defaultValue = "") String name) {
//        return name.equals("") ? "Hey!" : "Hey, " + name + "!";
//    }
//
//    @GetMapping("/{id}")
//    public String greeting2(@PathVariable String id) {
//        return "Hey, " + id + "!";
//    }

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;

    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", imageService.findAllImages());
        return Mono.just("index");
    }

    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity> oneRawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename).map(resource -> {
            try {
                return ResponseEntity.ok().body(new InputStreamResource(resource.getInputStream()));
            } catch(IOException e) {
                return ResponseEntity.badRequest().body("Can not find " + filename + " because exception: " + e.getMessage());
            }
        });
    }

    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono deleteFile(@PathVariable String filename) {
        return imageService.deleteOneImage(filename).then(Mono.just("redirect:/"));
    }

    @PostMapping(BASE_PATH)
    public Mono createFile(@RequestPart(name = "file") Flux<FilePart> files) {
        return imageService.createImage(files).then(Mono.just("redirect:/"));

    }

}
