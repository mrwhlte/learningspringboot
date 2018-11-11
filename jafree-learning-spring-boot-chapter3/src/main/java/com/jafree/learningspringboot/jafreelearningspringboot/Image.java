package com.jafree.learningspringboot.jafreelearningspringboot;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Image {

    @Id
    private String id;
    private String name;

    public Image(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
