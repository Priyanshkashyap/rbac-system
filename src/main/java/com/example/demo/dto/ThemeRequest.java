package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // maybe to modify before sending to db
public class ThemeRequest {

    private String theme;
}