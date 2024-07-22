package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.utils.Navigation;

public class AuthorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        Navigation.create(this);

        final String author = getIntent().getStringExtra("author");
        assert author != null;
        TextView authorFullName = findViewById(R.id.selectedItemTextView);
        authorFullName.setText(author);

        final int fid = getIntent().getIntExtra("fid", 0);
        final int mid = getIntent().getIntExtra("mid", 0);
        final int lid = getIntent().getIntExtra("lid", 0);

        Button buttonBooksBySeries = findViewById(R.id.books_by_series);
        buttonBooksBySeries.setOnClickListener(v -> {
            Intent intent = new Intent(this, SerieListActivity.class);
            intent.putExtra("author", author);
            intent.putExtra("queryType", "series_by_author");
            intent.putExtra("fid", fid);
            intent.putExtra("mid", mid);
            intent.putExtra("lid", lid);
            startActivity(intent);
        });
        Button buttonBooksWoSeries = findViewById(R.id.books_without_series);
        buttonBooksWoSeries.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("author", author);
            intent.putExtra("queryType", "books_without_series");
            intent.putExtra("fid", fid);
            intent.putExtra("mid", mid);
            intent.putExtra("lid", lid);
            startActivity(intent);
        });
        Button buttonBooksByGenres = findViewById(R.id.books_by_genres);
        buttonBooksByGenres.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("author", author);
            intent.putExtra("queryType", "books_by_genres");
            intent.putExtra("fid", fid);
            intent.putExtra("mid", mid);
            intent.putExtra("lid", lid);
            startActivity(intent);
        });
        Button buttonBooksByAlpha = findViewById(R.id.books_by_alphabet);
        buttonBooksByAlpha.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("author", author);
            intent.putExtra("queryType", "books_by_alphabet");
            intent.putExtra("fid", fid);
            intent.putExtra("mid", mid);
            intent.putExtra("lid", lid);
            startActivity(intent);
        });
        Button buttonBooksByDate = findViewById(R.id.books_by_date);
        buttonBooksByDate.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("author", author);
            intent.putExtra("queryType", "books_by_date");
            intent.putExtra("fid", fid);
            intent.putExtra("mid", mid);
            intent.putExtra("lid", lid);
            startActivity(intent);
        });

    }
}