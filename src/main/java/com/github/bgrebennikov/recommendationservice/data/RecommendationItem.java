package com.github.bgrebennikov.recommendationservice.data;

import java.util.Objects;
import java.util.UUID;

public class RecommendationItem {
    private UUID id;
    private String name;
    private String text;


    public RecommendationItem(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public RecommendationItem() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationItem that = (RecommendationItem) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, text);
    }
}

