package com.paulsoft.pelican.ranking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.paulsoft.pelican.ranking.commons.ImageCache;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.service.R;

import java.util.List;
import java.util.Objects;

public class RankElementListAdapter extends ArrayAdapter<RankElement> {

    private long currentAthleteId;

    public RankElementListAdapter(Context context, List<RankElement> objects, long currentAthleteId) {
        super(context, R.layout.table_rank_row, objects);
        this.currentAthleteId = currentAthleteId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.table_rank_row, parent, false);

        RankElement rankElement = getItem(position);

        if(rankElement.getAthleteId().equals(currentAthleteId)) {
            rowView.setBackgroundColor(R.color.sliver);
        }

        Bitmap avatar = ImageCache.get(rankElement.getAthleteId());

        if(Objects.nonNull(avatar)) {
            ImageView avatarView = rowView.findViewById(R.id.userAvatar);
            avatarView.setImageBitmap(avatar);
        }

        TextView placeElement = rowView.findViewById(R.id.place);
        placeElement.setText(rankElement.getPlace().toString());
        TextView loginElement = rowView.findViewById(R.id.login);
        loginElement.setText(rankElement.getName());
        TextView pointsElement = rowView.findViewById(R.id.points);
        pointsElement.setText(rankElement.getTotal() + " pts");
        TextView riddingElement = rowView.findViewById(R.id.ridding);
        riddingElement.setText(rankElement.getRide() + " km");
        TextView runningElement = rowView.findViewById(R.id.running);
        runningElement.setText(rankElement.getRun() + " km");

        return rowView;
    }

}
