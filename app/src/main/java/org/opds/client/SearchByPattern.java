package org.opds.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;

import java.util.List;

public class SearchByPattern extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_pattern);

        final String target = getInentExtraString("target");
        final String pattern = getInentExtraString("pattern");
        Wrapper.Result<List<String>> result = getSearchResult(target, pattern);
        if (result.isSuccess()) {
            TextView textView = findViewById(R.id.selectedItemTextView);
            textView.setText(pattern);
            List<String> items = result.getValue();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selected = items.get(position);
                Intent intent = new Intent(this, SearchByPattern.class);
                intent.putExtra("target", target);
                intent.putExtra("pattern", selected);
                startActivity(intent);
            });

        } else {
            TextView textView = findViewById(R.id.selectedItemTextView);
            textView.setText(result.getError());
        }

        Button buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    String getInentExtraString(String name) {
        final String value = getIntent().getStringExtra(name);
        return (value == null) ? "" : value;
    }

    Wrapper.Result<List<String>> getSearchResult(String target, String prefix) {
        AppContext app = (AppContext) getApplicationContext();
        if (target.equals("authors")) {
            return app.getApi().getAuthorsNextCharByPrefix(prefix);
        } else if (target.equals("series")) {
            return app.getApi().getSeriesNextCharByPrefix(prefix);
        } else {
            return Wrapper.Result.error("Empty target. Must be `authors` or `series`");
        }
    }
}