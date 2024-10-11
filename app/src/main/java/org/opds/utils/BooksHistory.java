package org.opds.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.opds.api.models.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        Set<String> sortedKeys = new TreeSet<>(Comparator.reverseOrder());
        sortedKeys.addAll(pref.getStringSet(KEYS, new TreeSet<>()));

        SharedPreferences.Editor editor = pref.edit();
        try {
            String jsonString = book.serialize();
            final String key = LocalDateTime.now().format(formatter);
            editor.putString(key, jsonString);
            sortedKeys.add(key);
            Log.d(TAG, "add(): <- " + key + " => " + book.toString());
        } catch (JsonProcessingException e) {
            Log.e(TAG, "add: -> " + e.getMessage());
            return;
        }

        Set<String> latest = new TreeSet<>(Comparator.reverseOrder());
        int count = 0;
        for (final String key : sortedKeys) {
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
        Log.e(TAG, "load(): -> sortedKeys: " + sortedKeys.toString());

        for (final String key : sortedKeys) {
            try {
                String jsonString = pref.getString(key, "");
                if (!jsonString.isEmpty()) {
                    Book book = Book.deserialize(jsonString);
                    Log.d(TAG, key + " -> " + book.toString());
                    books.add(book);
                }
            } catch (JsonProcessingException | RuntimeException e) {
                Log.e(TAG, "load(): -> " + e.getMessage());
            }
        }
        return books;
    }
}
