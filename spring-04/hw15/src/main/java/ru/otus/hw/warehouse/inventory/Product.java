package ru.otus.hw.warehouse.inventory;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public enum Product {
    MILK(UUID.fromString("96dd68c4-d5aa-4633-a90e-b1908405ae97"), "Молоко"),
    BREAD(UUID.fromString("4d08d88c-16ed-4ba1-88d4-f4375900a46e"), "Хлеб"),
    EGGS(UUID.fromString("6da5a7d5-6f35-4339-a5f9-d4dc21f5ff6d"), "Яйца"),
    CHEESE(UUID.fromString("7d0aefd9-8552-48d4-84fa-30c348b33bba"), "Сыр"),
    APPLE(UUID.fromString("4add70f8-3991-4eef-9b55-bfaf4641a25e"), "Яблоко"),
    BANANA(UUID.fromString("60014ade-2472-4657-9839-c434102ca7d9"), "Банан"),
    ORANGE(UUID.fromString("31427516-3743-4600-a2f5-5743ceb59d51"), "Апельсин"),
    POTATO(UUID.fromString("6537f2f8-6d37-42ce-b2a3-b6dac344c370"), "Картофель"),
    CABBAGE(UUID.fromString("1c20aa0f-d3bc-451c-9d52-2219d55c89dc"), "Капуста"),
    TOMATO(UUID.fromString("d6ee3662-9fff-4adf-8453-6ca74556dd56"), "Помидор"),
    RICE(UUID.fromString("21538773-8ad8-4d3c-97e0-e59f12c320da"), "Рис"),
    SUGAR(UUID.fromString("ae454c7d-87c0-404f-9e29-b3dd03012323"), "Сахар"),
    SALT(UUID.fromString("737efb36-303d-4df8-8e0b-a82cd5c05c8b"), "Соль"),
    WATER(UUID.fromString("0ca60fe0-6672-42a6-a42b-0c210d23b284"), "Вода"),
    COFFEE(UUID.fromString("27b8ffb3-195a-4808-9211-09e5ecab3ed6"), "Кофе");

    public static final List<UUID> ARTICLES = Arrays.stream(values())
            .map(product -> product.article)
            .collect(Collectors.toList());

    private static final Map<UUID, Product> ARTICLE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(product -> product.article, product -> product));


    private final UUID article;

    private final String name;

    Product(UUID article, String name) {
        this.article = article;
        this.name = name;
    }

    public static Product fromArticle(UUID article) {
        return ARTICLE_MAP.get(article);
    }
}
