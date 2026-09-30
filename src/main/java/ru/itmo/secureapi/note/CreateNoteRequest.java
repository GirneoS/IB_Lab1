package ru.itmo.secureapi.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 1000) String content) {
}

