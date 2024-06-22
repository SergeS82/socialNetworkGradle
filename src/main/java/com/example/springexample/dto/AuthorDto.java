package com.example.springexample.dto;

import com.example.springexample.dto.lib.Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
//@Builder не получится использовать т.к. json сериализация ломается
@Data
@Getter
@Setter
@EqualsAndHashCode
public class AuthorDto implements Dto {
    private String id;
    private String firstName;
    private String lastName;
    private String mail;
    private String phone;
    private Character sex;
    private String city;
}
