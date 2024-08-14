package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Author;
import org.opds.api.models.Book;
import org.opds.api.models.Pair;
import org.opds.api.models.Serie;
import org.opds.client.adapters.AuthorAdapter;
import org.opds.client.adapters.BookAdapter;
import org.opds.client.adapters.SerieAdapter;
import org.opds.utils.ErrorReporter;
import org.opds.utils.Navigation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchByPatternActivity extends AppCompatActivity {
    private static final String TAG = "org.opds.client.SearchByPatternActivity";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private ErrorReporter errorReporter = null;
    private AppContext app = null;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_pattern);
        Navigation.create(this);
        app = (AppContext) getApplicationContext();
        errorReporter = new ErrorReporter(this, TAG);

        target = getIntent().getStringExtra("target");
        assert target != null;

        String prefix = getIntent().getStringExtra("prefix");
        if (prefix == null) {
            prefix = "";
        }

        loadItems(prefix);

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setText(prefix);
        Navigation.afterTextChanged(searchEditText, new Navigation.AfterTextChanged() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String prefix = editable.toString();
                Log.d(TAG, "afterTextChanged: '" + prefix + "'");
                if (!prefix.isEmpty()) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            loadItems(prefix);
                        }
                    });
//                  loadItems(prefix);
                }
            }
        });
    }

    private void loadItems(final String prefix) {
        Log.d(TAG, "loadItems <- '" + prefix+ "'");
        Wrapper.Result<Pair<List<String>>> result = getSearchResult(prefix);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "loadItems: " + result.isSuccess());
                if (result.isSuccess()) {
                    TextView currentPrefix = findViewById(R.id.selectedItemTextView);
                    currentPrefix.setText(prefix);
                    loadFullMatched(findViewById(R.id.exactListView), result.getValue().first);
                    loadRestPrefixes(findViewById(R.id.nvcListView), result.getValue().second);
                } else {
                    errorReporter.report("loadItems()", result.getError());
                }
                Log.d(TAG, "loadItems: Done");
            }
        });
    }

    private Wrapper.Result<Pair<List<String>>> getSearchResult(String prefix) {
        switch (target) {
            case "authors":
                return app.getApi().getAuthorsByPrefix(prefix);
            case "series":
                return app.getApi().getSeriesByPrefix(prefix);
            case "books":
                return app.getApi().getBooksByPrefix(prefix);
            default:
                return Wrapper.Result.error("Empty target. Must be `authors` or `series`");
        }
    }

    private List<Author> loadMatchedAuthors(List<String> names) {
        return names.stream()
                .map(name -> app.getApi().getAuthorsByLastName(name))
                .filter(Wrapper.Result::isSuccess)
                .map(Wrapper.Result::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Serie> loadMatchedSeries(List<String> names) {
        return names.stream()
                .map(name -> app.getApi().getSeriesBySerieName(name))
                .filter(Wrapper.Result::isSuccess)
                .map(Wrapper.Result::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Book> loadMatchedBooks(List<String> names) {
        return names.stream()
                .map(name -> app.getApi().getBooksByBookTitle(name))
                .filter(Wrapper.Result::isSuccess)
                .map(Wrapper.Result::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private ArrayAdapter<?> getAdapter(List<String> items) {
        switch (target) {
            case "authors":
                return new AuthorAdapter(this, loadMatchedAuthors(items));
            case "series":
                return new SerieAdapter(this, loadMatchedSeries(items));
            case "books":
                return new BookAdapter(this, loadMatchedBooks(items));
            default:
                return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        }
    }

    private void loadFullMatched(ListView listView, List<String> strings) {
        List<String> items = strings.stream()
                .filter(item -> item != null && !item.trim().isEmpty())
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            ArrayAdapter<?> adapter = getAdapter(items);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Object item = adapter.getItem(position);

                if (item instanceof Author) {
                    Author author = (Author) item;
                    Intent intent = new Intent(this, AuthorActivity.class);
                    intent.putExtra("author", author.toString());
                    intent.putExtra("fid", author.first_name.id);
                    intent.putExtra("mid", author.middle_name.id);
                    intent.putExtra("lid", author.last_name.id);
                    startActivity(intent);
                }
                if (item instanceof Serie) {
                    Serie serie = (Serie) item;
                    Intent intent = new Intent(this, BookListActivity.class);
                    intent.putExtra("author", serie.author.toString());
                    intent.putExtra("queryType", "books_by_author_and_serie");
                    intent.putExtra("fid", serie.author.first_name.id);
                    intent.putExtra("mid", serie.author.middle_name.id);
                    intent.putExtra("lid", serie.author.last_name.id);
                    intent.putExtra("sid", serie.id);
                    startActivity(intent);
                }
                if (item instanceof Book) {
                    Book book = (Book) item;
                    Intent intent = new Intent(this, BookActivity.class);
                    intent.putExtra("id", book.id);
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