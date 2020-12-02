package com.paulsoft.pelican.ranking.model;

import java.io.Serializable;
import java.net.URI;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankElement implements Serializable {

    private String iconUrl;
    private String name;
    private Integer total;
    private Integer place;
    private Long athleteId;
    private Integer run;
    private Integer ride;

}
