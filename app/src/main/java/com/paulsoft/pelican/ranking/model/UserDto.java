package com.paulsoft.pelican.ranking.model;

import lombok.Data;

@Data
public class UserDto {

    private String name;
    private Long userId;

    @Override
    public String toString() {
        return name;
    }
}
