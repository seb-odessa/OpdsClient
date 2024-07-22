package org.opds.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.api.jni.Wrapper;
import org.opds.utils.Navigation;

import java.util.List;

public class MetaListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta);
        Navigation.create(this);

        AppContext app = (AppContext) getApplicationContext();
        Wrapper.Result<List<String>> result = app.getApi().getMetaGenres();
        loadItems(result);
    }

    private void loadItems(Wrapper.Result<List<String>> result) {
        if (result.isSuccess()) {
            List<String> items = result.getValue();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            ListView listView = findViewById(R.id.itemsView);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String item = adapter.getItem(position);
                assert item != null;
                TextView selectedItem = findViewById(R.id.selectedItemTextView);
                selectedItem.setText(item);
                Intent intent = new Intent(this, GenreListActivity.class);
                intent.putExtra("meta", item);
                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }
}