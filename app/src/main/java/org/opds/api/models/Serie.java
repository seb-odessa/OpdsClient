
package org.opds.api.models;
import java.util.Objects;

public class Serie {
    public int id;
    public String name;
    public int count;
    public Author author;

    public Serie(int id, String name, int count, Author author) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.author = author;
    }

    @Override
    public String toString() {
        return name + " [" + author + "] (" + count + ")";
    }

    // Метод equals()
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Serie serie = (Serie) o;
        return id == serie.id &&
                count == serie.count &&
                Objects.equals(name, serie.name) &&
                Objects.equals(author, serie.author);
    }

    // Метод hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(id, name, count, author);
    }
}