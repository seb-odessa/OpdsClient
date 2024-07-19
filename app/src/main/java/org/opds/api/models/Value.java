package org.opds.api.models;
import java.util.Objects;

public class Value {
    public int id;
    public String value;

    public Value(int id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Value value1 = (Value) o;
        return id == value1.id && Objects.equals(value, value1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}