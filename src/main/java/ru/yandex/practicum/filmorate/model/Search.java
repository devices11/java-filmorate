package ru.yandex.practicum.filmorate.model;

public enum Search {
    DIRECTOR,
    TITLE;

    public static Search SearchOrder(String str) {
        return switch (str.toLowerCase()) {
            case "director" -> DIRECTOR;
            case "title" -> TITLE;
            default -> null;
        };
    }
}
