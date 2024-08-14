package org.opds.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opds.api.jni.Wrapper;
import org.opds.client.databinding.ActivityMainBinding;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String TAG = "org.opds.client.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Wrapper.initLogging();

        org.opds.client.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        } else {
            initLibrary();
        }

        Button searchAuthors = findViewById(R.id.search_authors);
        searchAuthors.setOnClickListener(v -> openSearchByPatternActivity("authors"));

        Button searchSeries = findViewById(R.id.search_series);
        searchSeries.setOnClickListener(v -> openSearchByPatternActivity("series"));

        Button searchBooks = findViewById(R.id.search_titles);
        searchBooks.setOnClickListener(v -> openSearchByPatternActivity("books"));

        Button searchGenres = findViewById(R.id.search_by_genres);
        searchGenres.setOnClickListener(v -> openSearchByGenreActivity());

        Button lastBooks = findViewById(R.id.last_books);
        lastBooks.setOnClickListener(v -> openLastBooksActivity());

        Button lastAuthors = findViewById(R.id.last_authors);
        lastAuthors.setOnClickListener(v -> openLastAuthorsActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLibrary();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initLibrary() {
        File[] externalStorageFiles = getExternalFilesDirs(null);
        if (externalStorageFiles.length > 0) {
            for (File externalStorage : externalStorageFiles) {
                if (externalStorage != null) {
                    String storagePath = getStorageRoot(externalStorage.getAbsolutePath());
                    Log.d(TAG, "Storage: " + storagePath);

                    Wrapper.Result<List<String>> result = Wrapper.findLibraries(storagePath);
                    if (result.isSuccess()) {
                        for (String path : result.getValue()) {
                            Log.d(TAG, "Library: " + path);
                            String database = String.format("file:%s/books.db?mode=ro", path);
                            Log.d(TAG, "Library URI: " + database);
                            AppContext app = (AppContext) getApplicationContext();
                            app.setLibraryPath(path);
                            app.openDatabase(database);
                        }
                    } else {
                        Log.d(TAG, "Error: " + result.getError());
                    }
                } else {
                    Log.d(TAG, "No secondary external storage found.");
                }
            }
        } else {
            Log.d(TAG, "No external storage found.");
        }
    }

    public static String getStorageRoot(String path) {
        String[] segments = path.split("/");
        if (segments.length >= 3) {
            return "/" + segments[1] + "/" + segments[2];
        } else {
            return "";
        }
    }

    private void openSearchByPatternActivity(String target) {
        Intent intent = new Intent(this, SearchByPatternActivity.class);
        intent.putExtra("target", target);
        intent.putExtra("prefix", "");
        startActivity(intent);
    }

    private void openSearchByGenreActivity() {
        Intent intent = new Intent(this, MetaListActivity.class);
        startActivity(intent);
    }

    private void openLastBooksActivity() {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.putExtra("queryType", "books_history");
        startActivity(intent);
    }

    private void openLastAuthorsActivity() {
        Intent intent = new Intent(this, AuthorListActivity.class);
        intent.putExtra("queryType", "authors_history");
        startActivity(intent);
    }
}