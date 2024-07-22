package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Book;
import org.opds.client.adapters.BookAdapter;

import java.util.Comparator;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    public enum Sort {
        BY_TITLE,
        BY_DATE,
        BY_SERIE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        final String author = getIntent().getStringExtra("author");
        assert author != null;
        TextView selectedItem = findViewById(R.id.selectedItemTextView);
        selectedItem.setText(author);

        final int fid = getIntent().getIntExtra("fid", 0);
        final int mid = getIntent().getIntExtra("mid", 0);
        final int lid = getIntent().getIntExtra("lid", 0);
        final String queryType = getIntent().getStringExtra("queryType");
        assert queryType != null;
        AppContext app = (AppContext) getApplicationContext();
        switch (queryType) {
            case "books_by_author_and_serie": {
                final int sid = getIntent().getIntExtra("sid", 0);
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIdsAndSerieId(fid, mid, lid, sid);
                loadBooks(result, Sort.BY_SERIE);
                break;
            }
            case "books_without_series": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIdsWithoutSerie(fid, mid, lid);
                loadBooks(result, Sort.BY_TITLE);
                break;
            }
            case "books_by_alphabet": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIds(fid, mid, lid);
                loadBooks(result, Sort.BY_TITLE);
                break;
            }
            case "books_by_date": {
                Wrapper.Result<List<Book>> result = app.getApi().getBooksByAuthorIds(fid, mid, lid);
                loadBooks(result, Sort.BY_DATE);
                break;
            }
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

    private void loadBooks(Wrapper.Result<List<Book>> result, Sort sort) {
        if (result.isSuccess()) {
            List<Book> items = result.getValue();
            switch (sort) {
                case BY_DATE:
                    items.sort(Comparator.comparing(Book::getAdded).reversed());
                    break;
                case BY_SERIE:
                    items.sort(Comparator.comparingInt(Book::getSerieIndex));
                    break;
            }

            BookAdapter adapter = new BookAdapter(this, items);
            ListView listView = findViewById(R.id.itemsView);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Book item = adapter.getItem(position);
                assert item != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(item.toString());
//                Intent intent = new Intent(this, AuthorActivity.class);
//                intent.putExtra("author", author.toString());
//                intent.putExtra("fid", author.first_name.id);
//                intent.putExtra("mid", author.middle_name.id);
//                intent.putExtra("lid", author.last_name.id);
//                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }
}