package com.example.springexample.dto;

import com.example.springexample.dto.lib.Dto;
import lombok.Data;

import java.util.Map;

@Data
public class SubscriptionDto implements Dto {
    private String id;
    private String author;
    private String subscription;
}
