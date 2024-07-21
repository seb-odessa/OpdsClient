package org.opds.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.opds.api.models.Serie;

import java.util.List;

public class SerieAdapter extends ArrayAdapter<Serie> {

    public SerieAdapter(Context context, List<Serie> series) {
        super(context, 0, series);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Serie serie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);

        assert serie != null;
        textView.setText(serie.toString());

        return convertView;
    }
}