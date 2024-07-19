package org.opds.api.models;

import java.util.Locale;
import java.util.Objects;

public class Book {
    public int id;
    public String name;
    public int sid;
    public int idx;
    public Author author;
    public int size;
    public String added;

    public Book(int id, String name, int sid, int idx, Author author, int size, String added) {
        this.id = id;
        this.name = name;
        this.sid = sid;
        this.idx = idx;
        this.author = author;
        this.size = size;
        this.added = added;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (0 != idx) {
            sb.append(idx + " ");
        }
        sb.append(name + " - " + author + " (" + added + ") [" + format(size) + "]");

        return sb.toString();
    }

    public static final double KB = 1024.0;
    public static final double MB = 1024.0 * KB;

    private String format(int size) {
        if (size >= MB) {
            return String.format(Locale.US, "%.2f MB", size / MB);
        } else if (size >= KB) {
            return String.format(Locale.US, "%.2f KB", size / KB);
        } else {
            return String.format("%d B", size);
        }
    }

    // Метод equals()
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

    // Метод hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(id, name, sid, idx, author, size, added);
    }
}