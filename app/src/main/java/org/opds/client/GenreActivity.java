package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Book;
import org.opds.utils.Navigation;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        YearMonth currentMonth = YearMonth.now();
        LinearLayout linearLayout = findViewById(R.id.genre_buttons);
        AppContext app = (AppContext) getApplicationContext();
        int count = 0;
        for (int i = 1; i <= 24; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            String date = month.format(queryFmt);
            Wrapper.Result<List<Book>> result = app.getApi().getBooksByGenreIdAndDate(gid, date);
            if (result.isSuccess() && !result.getValue().isEmpty()) {
                ++count;
                linearLayout.addView(createButton(month, gid, date, genre));
            }
            if (count > 12) {
                break;
            }
        }
    }

    Button createButton(YearMonth month, int gid, String date, String genre) {
        Locale locale = new Locale("ru");
        DateTimeFormatter textFmt = DateTimeFormatter.ofPattern("Поступления за LLLL yyyy", locale);

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
            intent.putExtra("date", date);
            startActivity(intent);
        });

        return button;
    }

}