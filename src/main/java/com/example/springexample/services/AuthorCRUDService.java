package com.example.springexample.services;

import com.example.springexample.dto.AuthorDto;
import com.example.springexample.entity.Author;
import com.example.springexample.repository.AuthorRepository;
import com.example.springexample.services.lib.CRUDService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorCRUDService implements CRUDService<AuthorDto> {

    private final AuthorRepository repository;

    public AuthorCRUDService(AuthorRepository repository) {

        this.repository = repository;
    }

    @Override
    public AuthorDto getById(Long id) {
      Author author = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      return mapToDto(author);
    }

    @Override
    public List<AuthorDto> getTop(Long item, Integer count) {
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
    }
    @Override
    public void create(AuthorDto authorDto) {
        Author author = mapToEntity(authorDto);
        repository.save(author);
    }
    public AuthorDto createAuthor(AuthorDto authorDto) {

        return mapToDto(repository.save(mapToEntity(authorDto)));

    }

    @Override
    public void update(AuthorDto authorDto) {
        if (!repository.existsById(Long.getLong(authorDto.getId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.save(mapToEntity(authorDto));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    public static Author mapToEntity(AuthorDto authorDto) {
        return Author.builder()
                .id( Long.getLong(authorDto.getId()))
                .firstName(authorDto.getFirstName())
                .lastName(authorDto.getLastName())
                .mail(authorDto.getMail())
                .city(authorDto.getCity())
                .phone(authorDto.getPhone())
                .sex(authorDto.getSex())
                .isDeleted(false)
                .build();
    }

    public static AuthorDto mapToDto(Author author) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getId().toString());
        authorDto.setFirstName(author.getFirstName());
        authorDto.setLastName(author.getLastName());
        authorDto.setMail(author.getMail());
        authorDto.setSex(author.getSex());
        authorDto.setCity(author.getCity());
        authorDto.setPhone(author.getPhone());
        return authorDto;
    }

    public List<AuthorDto> getAll() {
        List<AuthorDto> dtos = new ArrayList<>();
        repository.findAll().forEach(v -> dtos.add(AuthorCRUDService.mapToDto(v)));
        return dtos;
    }
}
