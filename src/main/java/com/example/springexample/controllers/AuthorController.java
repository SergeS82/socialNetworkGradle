package com.example.springexample.controllers;

import com.example.springexample.dto.AuthorDto;
import com.example.springexample.services.AuthorCRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/author")
@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorCRUDService service;

    @PostMapping
    public AuthorDto create(@RequestBody AuthorDto authorDto) {
        return service.createAuthor(authorDto);
    }

    @GetMapping("/{id}")
    public AuthorDto get(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping()
    public List<AuthorDto> getaLL() {
        return service.getAll();
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody AuthorDto authorDto) {
        authorDto.setId(id);
        service.update(authorDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
