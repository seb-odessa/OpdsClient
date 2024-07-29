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
import org.opds.client.adapters.AuthorAdapter;
import org.opds.utils.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorListActivity extends AppCompatActivity {

    List<Author> items;
    List<Author> filtered;
    AuthorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        Navigation.create(this);

        final String queryType = getIntent().getStringExtra("queryType");
        assert queryType != null;

        AppContext app = (AppContext) getApplicationContext();
        switch (queryType) {
            case "authors_by_genre":
                final int gid = getIntent().getIntExtra("gid", 0);
                final String genre = getIntent().getStringExtra("genre");
                assert genre != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(genre);
                loadItems(app.getApi().getAuthorsByGenreId(gid));
                break;
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
                        items.stream().filter(item -> item.last_name.value.toLowerCase().startsWith(prefix))
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

    public void loadItems(Wrapper.Result<List<Author>> result) {
        if (result.isSuccess()) {
            items = result.getValue();
            filtered = new ArrayList<>(items);;
            adapter = new AuthorAdapter(this, filtered);
            ListView listView = findViewById(R.id.authors_of_book);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Author author = adapter.getItem(position);
                assert author != null;
                Intent intent = new Intent(this, AuthorActivity.class);
                intent.putExtra("author", author.toString());
                intent.putExtra("fid", author.first_name.id);
                intent.putExtra("mid", author.middle_name.id);
                intent.putExtra("lid", author.last_name.id);
                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }

}