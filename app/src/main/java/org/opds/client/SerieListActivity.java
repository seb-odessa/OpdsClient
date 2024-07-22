package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Author;
import org.opds.api.models.Serie;
import org.opds.client.adapters.SerieAdapter;
import org.opds.utils.Navigation;

import java.util.List;

public class SerieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        Navigation.create(this);

        final String author = getIntent().getStringExtra("author");
        if (author != null) {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(author);
        }

        AppContext app = (AppContext) getApplicationContext();
        final String queryType = getIntent().getStringExtra("queryType");
        assert queryType != null;
        switch (queryType) {
            case "series_by_author": {
                final int fid = getIntent().getIntExtra("fid", 0);
                final int mid = getIntent().getIntExtra("mid", 0);
                final int lid = getIntent().getIntExtra("lid", 0);
                Wrapper.Result<List<Serie>> result = app.getApi().getSeriesByAuthorIds(fid, mid, lid);
                loadSeries(result);
                break;
            }
        }
    }

    private void loadSeries(Wrapper.Result<List<Serie>> result) {
        if (result.isSuccess()) {
            List<Serie> series = result.getValue();
            SerieAdapter adapter = new SerieAdapter(this, series);
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