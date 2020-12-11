package com.paulsoft.pelican.ranking.model;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class RankElementWrapper {

    private RankElement rankElement;
    private Bitmap icon;

    public long getId() {
        return rankElement.getAthleteId();
    }

}
