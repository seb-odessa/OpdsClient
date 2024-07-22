package org.opds.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.opds.api.models.Value;

import java.util.List;

public class ValueAdapter extends ArrayAdapter<Value> {

    public ValueAdapter(Context context, List<Value> values) {
        super(context, 0, values);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Value value = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        assert value != null;
        textView.setText(value.value);
        return convertView;
    }
}
