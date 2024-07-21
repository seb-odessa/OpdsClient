package org.opds.client;

import android.content.Context;
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
import org.opds.api.models.Author;
import org.opds.api.models.Pair;
import org.opds.api.models.Serie;
import org.opds.client.adapters.AuthorAdapter;
import org.opds.client.adapters.SerieAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class SearchByPatternActivity extends AppCompatActivity {

    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_pattern);

        target = getIntent().getStringExtra("target");
        assert target != null;

        final String prefix = getIntent().getStringExtra("prefix");
        assert prefix != null;

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
        searchEditText.setText(prefix);
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

    private List<Author> loadMatchedAuthors(List<String> names) {
        AppContext app = (AppContext) getApplicationContext();
        return names.stream()
                .map(name -> app.getApi().getAuthorsByLastName(name))
                .filter(Wrapper.Result::isSuccess)
                .map(Wrapper.Result::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Serie> loadMatchedSeries(List<String> names) {
        AppContext app = (AppContext) getApplicationContext();
        return names.stream()
                .map(name -> app.getApi().getSeriesBySerieName(name))
                .filter(Wrapper.Result::isSuccess)
                .map(Wrapper.Result::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private ArrayAdapter<?> getAdapter(Context context, List<String> items) {
        if (target.equals("authors")) {
            return new AuthorAdapter(context, loadMatchedAuthors(items));
        } else if (target.equals("series")) {
            return new SerieAdapter(context, loadMatchedSeries(items));
        } else {
            return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        }
    }

    private void loadFullMatched(ListView listView, List<String> strings) {
        List<String> items = strings.stream()
                .filter(item -> item != null && !item.trim().isEmpty())
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            ArrayAdapter<?> adapter = getAdapter(this, items);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Object item = adapter.getItem(position);

                if (item instanceof Author) {
                    Author author = (Author) item;
                    Intent intent = new Intent(this, AuthorActivity.class);
                    intent.putExtra("author", author.toString());
                    startActivity(intent);
                }
                if (item instanceof Serie) {
                    Serie serie = (Serie) item;
                    Intent intent = new Intent(this, AuthorActivity.class);
                    intent.putExtra("serie", serie.toString());
                    startActivity(intent);
                }
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