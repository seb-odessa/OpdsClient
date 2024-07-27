package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.utils.Navigation;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
            Intent intent = new Intent(this, SerieListActivity.class);
            intent.putExtra("queryType", "series_by_genre");
            intent.putExtra("gid", gid);
            intent.putExtra("genre", genre);
            startActivity(intent);
        });

        Button buttonBooksByGenre = findViewById(R.id.books_by_genre);
        buttonBooksByGenre.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookListActivity.class);
            intent.putExtra("queryType", "books_by_genre");
            intent.putExtra("gid", gid);
            intent.putExtra("genre", genre);
            startActivity(intent);
        });

        DateTimeFormatter queryFmt = DateTimeFormatter.ofPattern("yyyy-MM-%");
        DateTimeFormatter textFmt = DateTimeFormatter.ofPattern("Поступления за LLLL yyyy", new Locale("ru"));

        YearMonth currentMonth = YearMonth.now();
        LinearLayout linearLayout = findViewById(R.id.genre_buttons);
        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            Button button = new Button(this);
            button.setText(month.format(textFmt));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, BookListActivity.class);
                intent.putExtra("queryType", "books_by_genre_and_date");
                intent.putExtra("gid", gid);
                intent.putExtra("genre", genre);
                intent.putExtra("date", month.format(queryFmt));
                startActivity(intent);
            });
            linearLayout.addView(button);
        }

    }
}