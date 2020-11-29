package com.paulsoft.pelican.ranking.model;

import java.net.URI;

import lombok.Data;

@Data
public class RankElement {

    private URI iconUrl;
    private String name;
    private Integer total;
    private Integer place;
    private Long athleteId;
    private Integer run;
    private Integer ride;
    private String login;

}
