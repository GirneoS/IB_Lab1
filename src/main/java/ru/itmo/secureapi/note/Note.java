package ru.itmo.secureapi.note;

import java.time.LocalDateTime;

public record Note(long id, String title, String content, LocalDateTime createdAt) {
}

