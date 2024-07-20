package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByPatternActivity extends AppCompatActivity {

    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_pattern);

        target = getInentExtraString("target");
        final String prefix = getInentExtraString("prefix");
        loadItems(prefix);

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

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String prefix = s.toString();
                if (!prefix.isEmpty()) {
                    loadItems(prefix);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void loadItems(String prefix) {
        Wrapper.Result<Pair<List<String>>> result = getSearchResult(prefix);
        TextView currentPrefix;
        if (result.isSuccess()) {
            currentPrefix = findViewById(R.id.selectedItemTextView);
            currentPrefix.setText(prefix);

            loadFullMatched(findViewById(R.id.exactListView), result.getValue().first);
            loadRestPrefixes(findViewById(R.id.nvcListView), result.getValue().second);
        } else {
            currentPrefix = findViewById(R.id.selectedItemTextView);
            currentPrefix.setText(result.getError());
        }
    }

    private String getInentExtraString(String name) {
        final String value = getIntent().getStringExtra(name);
        return (value == null) ? "" : value;
    }

    private Wrapper.Result<Pair<List<String>>> getSearchResult(String prefix) {
        AppContext app = (AppContext) getApplicationContext();
        if (target.equals("authors")) {
            return app.getApi().getAuthorsByPrefix(prefix);
        } else if (target.equals("series")) {
            return app.getApi().getSeriesByPrefix(prefix);
        } else {
            return Wrapper.Result.error("Empty target. Must be `authors` or `series`");
        }
    }
    private void loadFullMatched(ListView listView, List<String> strings) {
        List<String> items = strings.stream()
                .filter(item -> item != null && !item.trim().isEmpty())
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            ArrayAdapter<String> exactAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(exactAdapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selected = items.get(position);
                Intent intent = new Intent(this, SearchByPatternActivity.class);
                intent.putExtra("target", target);
                intent.putExtra("prefix", selected);
                startActivity(intent);
            });
        } else {
            listView.setVisibility(View.GONE);
        }
    }

    private void loadRestPrefixes(ListView listView, List<String> strings) {
        List<String> items = strings.stream()
                .filter(item -> item != null && !item.trim().isEmpty())
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            ArrayAdapter<String> nvcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(nvcAdapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selected = items.get(position);
                Intent intent = new Intent(this, SearchByPatternActivity.class);
                intent.putExtra("target", target);
                intent.putExtra("prefix", selected);
                startActivity(intent);
            });
        } else {
            listView.setVisibility(View.GONE);
        }
    }
}