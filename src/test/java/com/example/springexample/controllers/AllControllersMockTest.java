package com.example.springexample.controllers;


import com.example.springexample.dto.AuthorDto;
import com.example.springexample.services.AuthorCRUDService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AuthorController.class)
public class AllControllersMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorCRUDService authorCRUDService;

    @BeforeEach
    public void setUp() {
        // В случае необходимости вы можете выполнять дополнительную настройку перед тестами
    }

    @Test
    public void testSomeEndpoint() throws Exception {
        // Настраиваем поведения моков
        AuthorDto validDto = new AuthorDto();
        validDto.setFirstName("Иван");
        validDto.setLastName("Иванов");
        validDto.setMail("ivanov@gmail.com");
        validDto.setPhone("+79771111111");
        validDto.setSex('M');
        validDto.setCity("Таганрог");
        //
        Mockito.when(authorCRUDService.createAuthor(validDto)).thenReturn(validDto);
        // Используем mockMvc для выполнения запросов к контроллеру
        mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Иван\",\"lastName\": \"Иванов\",\"mail\": \"ivanov@gmail.com\",\"phone\": \"+79771111111\",\"sex\": \"M\",\"city\": \"Таганрог\"}"))
                .andExpect(status().isOk());
        // тут проверяем, что был передан корректный контент
        Mockito.verify(authorCRUDService, Mockito.times(1)).createAuthor(validDto);
        // а можно ещё и так, получить переданный контент, для ручной проверки
        ArgumentCaptor<AuthorDto> authorCaptor = ArgumentCaptor.forClass(AuthorDto.class);
        Mockito.verify(authorCRUDService, Mockito.times(1)).createAuthor(authorCaptor.capture());
        AuthorDto capturedDto = authorCaptor.getValue();
        Assertions.assertEquals(validDto, capturedDto);
    }
}
