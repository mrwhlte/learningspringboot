package com.jafree.learningspringboot.jafreelearningspringboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "upload-dir";

    private final ResourceLoader resourceLoader;
    private final ImageRepository imageRepository;

    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {

        this.resourceLoader = resourceLoader;
        this.imageRepository = imageRepository;
    }

    public Flux<Image> findAllImages() {
        return imageRepository.findAll()
                .log("find all~");
    }

    public Mono<Resource> findOneImage(String filename) {
        return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename))
                .log("find one~");

    }

    public Mono deleteOneImage(String filename) {

        Mono<Void> deleteDbImage = imageRepository.findByName(filename)
                .log("find one to delete~")
                .flatMap(imageRepository::delete)
                .log("delete one from db~");

        Mono<Object> deleteFile = Mono.fromRunnable( () -> {
            try{
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT + "/" + filename));
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }).log("delete one file~");

        return Mono.when(deleteDbImage, deleteFile)
                .log("delete when~")
                .then()
                .log("delete done~");
    }

    public Mono<Void> createImage(Flux<FilePart> files) {

        return files.flatMap(file -> {
            Mono<Image> saveDbImage = imageRepository.save(new Image(UUID.randomUUID().toString(), file.filename()))
                    .log("save to db~");
            Mono<Void> copyFile = file.transferTo(Paths.get(UPLOAD_ROOT + "/" +file.filename()).toFile())
                    .log("save file~");

            return Mono.when(saveDbImage, copyFile).log("save when~");
        })
                .log("save flatmap~")
                .then()
                .log("save done~");


    }

    @Bean
    CommandLineRunner setUp() throws IOException {

        return args -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("pic 1", new FileWriter(UPLOAD_ROOT + "/first.jpg"));
            FileCopyUtils.copy("pic 2", new FileWriter(UPLOAD_ROOT + "/second.png"));
            FileCopyUtils.copy("pic 3", new FileWriter(UPLOAD_ROOT + "/third.jpg"));
        };
    }
}
