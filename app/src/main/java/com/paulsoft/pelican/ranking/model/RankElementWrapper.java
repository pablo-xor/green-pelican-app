package com.paulsoft.pelican.ranking.model;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankElementWrapper implements Serializable {

    private RankElement rankElement;
    private Bitmap icon;

    public long getId() {
        return rankElement.getAthleteId();
    }

    public boolean hasUserAvatar() {
        return null != icon;
    }

}
