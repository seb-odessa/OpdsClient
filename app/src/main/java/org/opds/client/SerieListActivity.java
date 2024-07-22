package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Author;
import org.opds.api.models.Serie;
import org.opds.client.adapters.SerieAdapter;
import org.opds.utils.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SerieListActivity extends AppCompatActivity {
    List<Serie> items;
    List<Serie> filtered;
    SerieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        Navigation.create(this);


        AppContext app = (AppContext) getApplicationContext();
        final String queryType = getIntent().getStringExtra("queryType");
        assert queryType != null;
        switch (queryType) {
            case "series_by_author": {
                final String author = getIntent().getStringExtra("author");
                assert author != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(author);
                final int fid = getIntent().getIntExtra("fid", 0);
                final int mid = getIntent().getIntExtra("mid", 0);
                final int lid = getIntent().getIntExtra("lid", 0);
                loadItems(app.getApi().getSeriesByAuthorIds(fid, mid, lid));
                break;
            }
            case "series_by_genre": {
                final String genre = getIntent().getStringExtra("genre");
                assert genre != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(genre);
                final int gid = getIntent().getIntExtra("gid", 0);
                loadItems(app.getApi().getSeriesByGenreId(gid));
                break;
            }

        }

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String prefix = s.toString().toLowerCase();

                filtered =
                        items.stream().filter(item -> item.name.toLowerCase().startsWith(prefix))
                                .collect(Collectors.toList());

                adapter.clear();
                adapter.addAll(filtered);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void loadItems(Wrapper.Result<List<Serie>> result) {
        if (result.isSuccess()) {
            items = result.getValue();
            filtered = new ArrayList<>(items);
            adapter = new SerieAdapter(this, filtered);
            ListView listView = findViewById(R.id.seriesView);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Serie serie = adapter.getItem(position);
                assert serie != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(serie.toString());
                Intent intent = new Intent(this, BookListActivity.class);
                Author author = serie.author;
                intent.putExtra("author", author.toString());
                intent.putExtra("queryType", "books_by_author_and_serie");
                intent.putExtra("fid", author.first_name.id);
                intent.putExtra("mid", author.middle_name.id);
                intent.putExtra("lid", author.last_name.id);
                intent.putExtra("sid", serie.id);
                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }
}