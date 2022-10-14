package com.example.jackthief.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"suit", "image", "images"})
public class Card {
    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public Card setCode(String code) {
        this.code = code;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Card setValue(String value) {
        this.value = value;
        return this;
    }


    @Override
    public String toString() {
        return "Card{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
