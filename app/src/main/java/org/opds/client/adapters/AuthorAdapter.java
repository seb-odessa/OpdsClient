package org.opds.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.opds.api.models.Author;

import java.util.List;

public class AuthorAdapter extends ArrayAdapter<Author> {

    public AuthorAdapter(Context context, List<Author> authors) {
        super(context, 0, authors);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Author author = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);

        assert author != null;
        textView.setText(author.toString());

        return convertView;
    }
}