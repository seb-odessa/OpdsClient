package org.opds.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.client.databinding.ActivityMainBinding;
import org.opds.utils.FileUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        org.opds.client.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String dbPath = FileUtils.copyAssetToInternalStorage(this, "books.db");
        String uri = String.format("file:%s?mode=ro", dbPath);
        AppContext app = (AppContext) getApplicationContext();
        app.reset(uri);

        Button searchAuthors = findViewById(R.id.search_authors);
        searchAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchByPatternActivity("authors");
            }
        });

        Button searchSeries = findViewById(R.id.search_series);
        searchSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchByPatternActivity("series");
            }
        });

        final Context ctx = this;
        Button searchGenres = findViewById(R.id.search_by_genres);
        searchGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ctx, MetaListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openSearchByPatternActivity(String target) {
        Intent intent = new Intent(this, SearchByPatternActivity.class);
        intent.putExtra("target", target);
        intent.putExtra("prefix", "");
        startActivity(intent);
    }
}