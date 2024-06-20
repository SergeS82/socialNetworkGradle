package com.example.springexample.dto.lib;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DtoIdMixin {
    @JsonIgnore
    Long getId();
}
