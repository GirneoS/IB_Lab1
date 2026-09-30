package ru.itmo.secureapi.note;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/data")
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public List<NoteResponse> list(Principal principal) {
        return noteRepository.findAllForUser(principal.getName()).stream()
                .map(NoteController::safeResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody CreateNoteRequest request, Principal principal) {
        return safeResponse(noteRepository.createForUser(
                principal.getName(), request.title().strip(), request.content().strip()));
    }

    private static NoteResponse safeResponse(Note note) {
        return new NoteResponse(
                note.id(),
                HtmlUtils.htmlEscape(note.title()),
                HtmlUtils.htmlEscape(note.content()),
                note.createdAt());
    }
}

