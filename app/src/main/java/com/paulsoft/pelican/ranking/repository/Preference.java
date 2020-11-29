package com.paulsoft.pelican.ranking.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Preference {

    LOGIN("usrLogin");

    private String key;

}
