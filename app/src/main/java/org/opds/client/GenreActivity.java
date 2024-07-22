package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.utils.Navigation;

public class GenreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        Navigation.create(this);

        final int gid = getIntent().getIntExtra("gid", 0);
        final String genre = getIntent().getStringExtra("genre");
        assert genre != null;

        TextView selectedItem = findViewById(R.id.selectedItemTextView);
        selectedItem.setText(genre);

        Button buttonAuthorsByGenre = findViewById(R.id.authors_by_genre);
        buttonAuthorsByGenre.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorListActivity.class);
            intent.putExtra("queryType", "authors_by_genre");
            intent.putExtra("gid", gid);
            intent.putExtra("genre", genre);
            startActivity(intent);
        });

        Button buttonSeriesByGenre = findViewById(R.id.series_by_genre);
        buttonSeriesByGenre.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorListActivity.class);
            intent.putExtra("gid", gid);
            intent.putExtra("genre", genre);
            startActivity(intent);
        });

        Button buttonBooksByGenre = findViewById(R.id.books_by_genre);
        buttonBooksByGenre.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("gid", gid);
            intent.putExtra("genre", genre);
            startActivity(intent);
        });

    }
}