package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Value;
import org.opds.client.adapters.ValueAdapter;

import java.util.List;

public class GenreListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        final String meta = getIntent().getStringExtra("meta");
        assert meta != null;

        AppContext app = (AppContext) getApplicationContext();
        Wrapper.Result<List<Value>> result = app.getApi().getGenresByMeta(meta);
        loadItems(result);

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
    private void loadItems(Wrapper.Result<List<Value>> result) {
        if (result.isSuccess()) {
            List<Value> items = result.getValue();
            ValueAdapter adapter = new ValueAdapter(this,  items);
            ListView listView = findViewById(R.id.itemsView);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Value item = adapter.getItem(position);
                assert item != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(item.toString());
//                Intent intent = new Intent(this, GenreActivity.class);
//                intent.putExtra("meta", item);
//                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }
}