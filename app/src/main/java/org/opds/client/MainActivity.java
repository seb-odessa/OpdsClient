package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.opds.utils.FileUtils;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.client.databinding.ActivityMainBinding;


import android.view.View;


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
    }

    private void openSearchByPatternActivity(String target) {
        Intent intent = new Intent(this, SearchByPatternActivity.class);
        intent.putExtra("target", target);
        startActivity(intent);
    }
}