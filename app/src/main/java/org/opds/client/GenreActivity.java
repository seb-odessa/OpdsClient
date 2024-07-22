package org.opds.client;

import android.os.Bundle;
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

    }
}