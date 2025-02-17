package org.opds.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Book;
import org.opds.client.adapters.BookAdapter;
import org.opds.utils.BooksHistory;
import org.opds.utils.Navigation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class BookListActivity extends AppCompatActivity {
    private static final String TAG = "org.opds.client.BookListActivity";
    List<Book> items;
    List<Book> filtered;
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Navigation.create(this);

        TextView selectedItem = findViewById(R.id.selectedItemTextView);
        final String author = getIntent().getStringExtra("author");
        final String genre = getIntent().getStringExtra("genre");
        if (author != null) {
            selectedItem.setText(author);
        } else if (genre != null) {
            selectedItem.setText(genre);
        }

        final int fid = getIntent().getIntExtra("fid", 0);
        final int mid = getIntent().getIntExtra("mid", 0);
        final int lid = getIntent().getIntExtra("lid", 0);
        final int sid = getIntent().getIntExtra("sid", 0);
        final int gid = getIntent().getIntExtra("gid", 0);

        final String queryType = getIntent().getStringExtra("queryType");
        assert queryType != null;
        AppContext app = (AppContext) getApplicationContext();
        switch (queryType) {
            case "books_by_author_and_serie": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIdsAndSerieId(fid, mid, lid, sid);
                loadItems(result, Sort.RESORT_BY_SERIE_INDEX);
                break;
            }
            case "books_without_series": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIdsWithoutSerie(fid, mid, lid);
                loadItems(result, Sort.KEEP_ORIGINAL_ORDER);
                break;
            }
            case "books_by_alphabet": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIds(fid, mid, lid);
                loadItems(result, Sort.KEEP_ORIGINAL_ORDER);
                break;
            }
            case "books_by_date": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIds(fid, mid, lid);
                loadItems(result, Sort.RESORT_BY_DATE);
                break;
            }
            case "books_by_genre": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByGenreIdAndDate(gid, "%");
                loadItems(result, Sort.KEEP_ORIGINAL_ORDER);
                break;
            }
            case "books_by_genre_and_date": {
                final String date = getIntent().getStringExtra("date");
                assert date != null;
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByGenreIdAndDate(gid, date);
                loadItems(result, Sort.RESORT_BY_DATE);
                break;
            }
            case "books_history": {
                SharedPreferences pref = getSharedPreferences(BooksHistory.TAG, Context.MODE_PRIVATE);
                loadItems(BooksHistory.load(pref), Sort.KEEP_ORIGINAL_ORDER);
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

    private void loadItems(Wrapper.Result<List<Book>> result, Sort sort) {
        if (result.isSuccess()) {
            loadItems(result.getValue(), sort);
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }

    private void loadItems(List<Book> items, Sort sort) {
        switch (sort) {
            case RESORT_BY_DATE:
                items.sort(Comparator.comparing(Book::getAdded).reversed());
                break;
            case RESORT_BY_SERIE_INDEX:
                items.sort(Comparator.comparingInt(Book::getSerieIndex));
                break;
        }
        filtered = new ArrayList<>(items);
        adapter = new BookAdapter(this, filtered);
        ListView listView = findViewById(R.id.authors_of_book);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Book book = adapter.getItem(position);
            assert book != null;
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(book.toString());
            Intent intent = new Intent(this, BookActivity.class);
            intent.putExtra("id", book.id);
            startActivity(intent);
        });

    }

    public enum Sort {
        KEEP_ORIGINAL_ORDER,
        RESORT_BY_DATE,
        RESORT_BY_SERIE_INDEX
    }
}