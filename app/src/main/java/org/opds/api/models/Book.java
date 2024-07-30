package org.opds.api.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    public static final double KB = 1024.0;
    public static final double MB = 1024.0 * KB;
    public int id;
    public String name;
    public int sid;
    public int idx;
    public Author author;
    public int size;
    public String added;

    public Book() {
    }

    public Book(int id, String name, int sid, int idx, Author author, int size, String added) {
        this.id = id;
        this.name = name;
        this.sid = sid;
        this.idx = idx;
        this.author = author;
        this.size = size;
        this.added = added;
    }

    @SuppressLint("DefaultLocale")
    public static String format(int size) {
        if (size >= MB) {
            return String.format(Locale.US, "%.2f MB", size / MB);
        } else if (size >= KB) {
            return String.format(Locale.US, "%.2f KB", size / KB);
        } else {
            return String.format("%d B", size);
        }
    }

    public String getTitle() {
        return (idx > 0)
                ? String.format(Locale.US, "%d. %s", idx, name)
                : name;
    }

    public String getAdded() {
        return added;
    }

    public int getSerieIndex() {
        return idx;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (0 != idx) {
            sb.append(idx).append(" ");
        }
        sb.append(name).append(" - ").append(author).append(" (").append(added).append(") [").append(format(size)).append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book = (Book) o;
        return id == book.id &&
                size == book.size &&
                Objects.equals(name, book.name) &&
                Objects.equals(sid, book.sid) &&
                Objects.equals(idx, book.idx) &&
                Objects.equals(author, book.author) &&
                Objects.equals(added, book.added);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sid, idx, author, size, added);
    }

    public String serialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static Book deserialize(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Book.class);
    }
}