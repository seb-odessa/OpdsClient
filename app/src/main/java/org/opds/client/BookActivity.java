package org.opds.client;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.opds.api.jni.Wrapper;
import org.opds.api.models.Author;
import org.opds.api.models.Book;
import org.opds.client.adapters.AuthorAdapter;
import org.opds.client.adapters.BookAdapter;
import org.opds.utils.BooksHistory;
import org.opds.utils.Navigation;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BookActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 1;
    private static final String AUTHORITY = "org.opds.client.provider";
    private static final String TAG = "org.opds.client.BookActivity";

    private AppContext app = null;
    Book book = null;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Navigation.create(this);
        TextView selected = findViewById(R.id.selectedItemTextView);

        app = (AppContext) getApplicationContext();
        final int bid = getIntent().getIntExtra("id", 0);
        Wrapper.Result<Book> result = app.getApi().getBookById(bid);
        if (result.isSuccess()) {
            book = result.getValue();
            TextView bookFile = findViewById(R.id.book_id);
            bookFile.setText(String.format("Id: %d", book.id));
            TextView bookTitle = findViewById(R.id.book_title);
            bookTitle.setText(book.getTitle());
            selected.setText(book.getTitle());

            TextView bookSize = findViewById(R.id.book_size);
            bookSize.setText(String.format("File size: %s", Book.format(book.size)));
            TextView bookAdded = findViewById(R.id.book_added);
            bookAdded.setText(String.format("Added to the library: %s", book.added));

            loadAuthors(app.getApi().getAuthorsByBooksIds(new int[]{book.id}));
            loadBooks(app.getApi().getBooksBySerieId(book.sid));

            Button buttonRead = findViewById(R.id.read_book);
            buttonRead.setOnClickListener(v -> {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        requestPermission();
                    } else {
                        extractBookFromArchive();
                    }
                } else {
                    extractBookFromArchive();
                }
            });
        } else {
            selected.setText(result.getError());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    extractBookFromArchive();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            } else {
                extractBookFromArchive();
            }
        }
    }

    private void extractBookFromArchive() {
        final String libraryPath = app.getLibraryPath();
        assert book != null;
        final String bookFile = String.format("%s.fb2", book.id);
        Log.d(TAG, "Book Id: " + book.id);
        Log.d(TAG, "Book File: " + bookFile);
        Log.d(TAG, "Library Path: " + libraryPath);

        TextView selected = findViewById(R.id.selectedItemTextView); 
        Wrapper.Result<List<String>> result = Wrapper.findArchives(libraryPath, book.id);
        if (result.isSuccess()) {
            List<String> archives = result.getValue();
            for(String archive: archives) {
                Log.d(TAG, "Library Archive: " + archive);
                File destinationDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (destinationDir != null) {
                    final String destination;
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        destination = Environment.getExternalStorageDirectory().getPath() + "/Books";
                    } else {
                        destination = destinationDir.getAbsolutePath();
                    }
                    Log.d(TAG, "Book destination: " + destination);
                    Wrapper.Result<String> maybeOk = Wrapper.extractFile(archive, bookFile, destination);
                    if (maybeOk.isSuccess()) {
                        File path = new File(destination + "/" +  bookFile);
                        Log.d(TAG, "File destination: " + path.getAbsolutePath());
                        openFileWithOtherApp(path);
                        SharedPreferences pref = getSharedPreferences(BooksHistory.TAG, Context.MODE_PRIVATE);
                        BooksHistory.add(pref, book);
                    } else {
                        selected.setText(maybeOk.getError());
                    }
                }
            }
        } else {
            selected.setText(result.getError());
        }
    }

    private void openFileWithOtherApp(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, AUTHORITY, file);
        Log.d(TAG, "openFileWithOtherApp <- " + fileUri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "No application found to open the file.", e);
            Toast.makeText(this, "No application found to open the file.", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadAuthors(Wrapper.Result<List<Author>> result) {
        if (result.isSuccess()) {
            List<Author> items = result.getValue();
            AuthorAdapter adapter = new AuthorAdapter(this, items);
            ListView listView = findViewById(R.id.authors_of_book);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Author author = adapter.getItem(position);
                assert author != null;
                Intent intent = new Intent(this, AuthorActivity.class);
                intent.putExtra("author", author.toString());
                intent.putExtra("fid", author.first_name.id);
                intent.putExtra("mid", author.middle_name.id);
                intent.putExtra("lid", author.last_name.id);
                startActivity(intent);
            });
        } else {
            TextView selectedItem = findViewById(R.id.selectedItemTextView);
            selectedItem.setText(result.getError());
        }
    }

    public void loadBooks(Wrapper.Result<List<Book>> result) {
        TextView selectedItem = findViewById(R.id.selectedItemTextView);
        if (result.isSuccess()) {
            List<Book> items = result.getValue();
            BookAdapter adapter = new BookAdapter(this, items);
            ListView listView = findViewById(R.id.books_in_serie);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Book book = adapter.getItem(position);
                assert book != null;
                Intent intent = new Intent(this, BookActivity.class);
                intent.putExtra("id", book.id);
                startActivity(intent);
            });
        } else {
            selectedItem.setText(result.getError());
        }
    }
}