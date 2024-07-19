package org.opds.api.models;

import java.util.Objects;

public class Author {
    public Value first_name;
    public Value middle_name;
    public Value last_name;
    public Author(Value first_name, Value middle_name, Value last_name) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        appendValue(sb, first_name);
        appendValue(sb, middle_name);
        appendValue(sb, last_name);

        return sb.toString();
    }

    private void appendValue(StringBuilder sb, Value value) {
        if (value != null && value.value != null && !value.value.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(value.value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(first_name, author.first_name) &&
                Objects.equals(middle_name, author.middle_name) &&
                Objects.equals(last_name, author.last_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first_name, middle_name, last_name);
    }
}