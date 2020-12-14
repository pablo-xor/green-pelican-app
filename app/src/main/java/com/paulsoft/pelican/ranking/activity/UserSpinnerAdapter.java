package com.paulsoft.pelican.ranking.activity;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserSpinnerAdapter extends ArrayAdapter<UserDto> {

    private List<Long> ids;

    public UserSpinnerAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
    }

    public void updateData(List<RankElement> rank) {
        List<UserDto> users = convertRankListToUserList(rank);

        clear();
        addAll(users);
        ids = users.stream().map(el -> el.getUserId()).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    private List<UserDto> convertRankListToUserList(List<RankElement> rank) {
        return rank.stream()
                .map(this::convertSingle)
                .collect(Collectors.toList());
    }

    public int getPosition(Long userId) {
        return ids.indexOf(userId);
    }

    private UserDto convertSingle(RankElement rankElement) {
        UserDto user = new UserDto();
        user.setName(rankElement.getName());
        user.setUserId(rankElement.getAthleteId());

        return user;
    }

}
