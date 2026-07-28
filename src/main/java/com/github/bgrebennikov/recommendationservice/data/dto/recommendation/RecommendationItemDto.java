package com.github.bgrebennikov.recommendationservice.data.dto.recommendation;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO, описывающий отдельное рекомендуемое банковское предложение для пользователя.
 *
 * @author Ekaterina, Boris
 * @version 1.0
 */
public class RecommendationItemDto {

    private UUID id;
    private String name;
    private String text;

    /**
     * @param id   Уникальный идентификатор рекомендуемого продукта
     * @param name Наименование продукта
     * @param text Рекламное описание или текст рекомендации
     */
    public RecommendationItemDto(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public RecommendationItemDto() {
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
        RecommendationItemDto that = (RecommendationItemDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, text);
    }
}