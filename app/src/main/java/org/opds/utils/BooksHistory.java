package org.opds.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.opds.api.models.Book;
import org.opds.client.AppContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
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
import org.opds.utils.Navigation;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class BooksHistory {
    public static final String TAG = "org.opds.client.utils.BooksHistory";
    public static final String KEYS = "org.opds.client.utils.BooksHistory.KEYS";
    private static final int MAX_COUNT = 10;

    public static void add(SharedPreferences pref, Book book) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        SharedPreferences.Editor editor = pref.edit();
        Set<String> keys = new TreeSet<>(pref.getStringSet(KEYS, new TreeSet<>()));
        try {
            String jsonString = book.serialize();
            final String key = LocalDateTime.now().format(formatter);
            editor.putString(key, jsonString);
            keys.add(key);
            Log.d(TAG, "add(): <- {" + key + ", " + book.getTitle() + "}");
        } catch (JsonProcessingException e) {
            Log.e(TAG, "add: -> " + e.getMessage());
            return;
        }

        Set<String> latest = new TreeSet<>();
        int count = 0;
        for (final String key : keys) {
            if (count <= MAX_COUNT) {
                latest.add(key);
            } else {
                editor.remove(key);
            }
            count++;
        }
        editor.putStringSet(KEYS, latest);
        editor.apply();
    }

    public static List<Book> load(SharedPreferences pref) {
        List<Book> books = new ArrayList<>();
        TreeSet<String> sortedKeys = new TreeSet<>(Comparator.reverseOrder());
        sortedKeys.addAll(pref.getStringSet(KEYS, new TreeSet<>()));

        for (final String key : sortedKeys) {
            try {
                String jsonString = pref.getString(key, "");
                if (!jsonString.isEmpty()) {
                    Book book = Book.deserialize(jsonString);
                    Log.d(TAG, key + " -> " + book.getTitle());
                    books.add(book);
                }
            } catch (JsonProcessingException | RuntimeException e) {
                Log.e(TAG, "load(): -> " + e.getMessage());
            }
        }
        return books;
    }
}
