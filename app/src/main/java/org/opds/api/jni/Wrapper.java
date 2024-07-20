package org.opds.api.jni;

import org.opds.api.models.*;
import java.util.List;

public class Wrapper {
    static {
        System.loadLibrary("opds_jni");
    }

    public OpdsApi create(String dbPath) {
        return new OpdsApi(createOpdsApi(dbPath));
    }

    public static class OpdsApi {
        private long ptr = 0;

        private OpdsApi(long ptr) {
            this.ptr = ptr;
        }

        public void close() {
            Wrapper.destroyOpdsApi(this.ptr);
            this.ptr = 0;
        }

        public boolean isReadonly() {
            return Wrapper.isReadonly(this.ptr);
        }

        public Wrapper.Result<List<String>> getAuthorsNextCharByPrefix(String prefix) {
            return Wrapper.getAuthorsNextCharByPrefix(this.ptr, prefix);
        }

        public Wrapper.Result<List<String>> getSeriesNextCharByPrefix(String prefix) {
            return Wrapper.getSeriesNextCharByPrefix(this.ptr, prefix);
        }

        public Wrapper.Result<List<Author>> getAuthorsByLastName(String name) {
            return Wrapper.getAuthorsByLastName(this.ptr, name);
        }

        public Wrapper.Result<List<Serie>> getSeriesBySerieName(String name) {
            return Wrapper.getSeriesBySerieName(this.ptr, name);
        }

        public Wrapper.Result<List<Serie>> getSeriesByGenreId(int id) {
            return Wrapper.getSeriesByGenreId(this.ptr, id);
        }

        public Wrapper.Result<List<Author>> getAuthorsByGenreId(int id) {
            return Wrapper.getAuthorsByGenreId(this.ptr, id);
        }

        public Wrapper.Result<List<Author>> getAuthorsByBooksIds(int[] ids) {
            return Wrapper.getAuthorsByBooksIds(this.ptr, ids);
        }

        public Wrapper.Result<List<Book>> getBooksByGenreIdAndDate(int id, String date) {
            return Wrapper.getBooksByGenreIdAndDate(this.ptr, id, date);
        }

        public Wrapper.Result<List<Serie>> getSeriesByAuthorIds(int fid, int mid, int lid) {
            return Wrapper.getSeriesByAuthorIds(this.ptr, fid, mid, lid);
        }

        public Wrapper.Result<List<Value>> getMetaGenres() {
            return Wrapper.getMetaGenres(this.ptr);
        }

        public Wrapper.Result<List<Value>> getGenresByMeta(String name) {
            return Wrapper.getGenresByMeta(this.ptr, name);
        }

        public Wrapper.Result<Author> getAuthorByIds(int fid, int mid, int lid) {
            return Wrapper.getAuthorByIds(this.ptr, fid, mid, lid);
        }

        public Wrapper.Result<List<Book>> getBooksByAuthorIds(int fid, int mid, int lid) {
            return Wrapper.getBooksByAuthorIds(this.ptr, fid, mid, lid);
        }

        public Wrapper.Result<List<Book>> getBooksByAuthorIdsWithoutSerie(int fid, int mid, int lid) {
            return Wrapper.getBooksByAuthorIdsWithoutSerie(this.ptr, fid, mid, lid);
        }

        public Wrapper.Result<List<Book>> getBooksByAuthorIdsAndSerieId(int fid, int mid, int lid, int sid) {
            return Wrapper.getBooksByAuthorIdsAndSerieId(this.ptr, fid, mid, lid, sid);
        }

        public Wrapper.Result<List<Book>> getBooksBySerieId(int sid) {
            return Wrapper.getBooksBySerieId(this.ptr, sid);
        }

        public Wrapper.Result<Pair<List<String>>> getAuthorsByPrefix(String prefix) {
            return Wrapper.getAuthorsByPrefix(this.ptr, prefix);
        }

        public Wrapper.Result<Pair<List<String>>> getSeriesByPrefix(String prefix) {
            return Wrapper.getSeriesByPrefix(this.ptr, prefix);
        }
    }

    public static native long createOpdsApi(String dbPath);

    public static native void destroyOpdsApi(long api);

    public static native boolean isReadonly(long api);

    public static native Result<List<String>> getAuthorsNextCharByPrefix(long api, String prefix);

    public static native Result<Pair<List<String>>> getAuthorsByPrefix(long api, String prefix);

    public static native Result<List<Author>> getAuthorsByLastName(long api, String name);

    public static native Result<List<Author>> getAuthorsByGenreId(long api, int id);

    public static native Result<List<Author>> getAuthorsByBooksIds(long api, int[] ids);

    public static native Result<Author> getAuthorByIds(long api, int fid, int mid, int lid);

    public static native Result<List<String>> getSeriesNextCharByPrefix(long api, String prefix);

    public static native Result<Pair<List<String>>> getSeriesByPrefix(long api, String prefix);

    public static native Result<List<Serie>> getSeriesBySerieName(long api, String name);

    public static native Result<List<Serie>> getSeriesByGenreId(long api, int id);

    public static native Result<List<Serie>> getSeriesByAuthorIds(long api, int fid, int mid, int lid);

    public static native Result<List<Value>> getMetaGenres(long api);

    public static native Result<List<Value>> getGenresByMeta(long api, String name);

    public static native Result<List<Book>> getBooksByGenreIdAndDate(long api, int id, String date);

    public static native Result<List<Book>> getBooksByAuthorIds(long api, int fid, int mid, int lid);

    public static native Result<List<Book>> getBooksByAuthorIdsWithoutSerie(long api, int fid, int mid, int lid);

    public static native Result<List<Book>> getBooksByAuthorIdsAndSerieId(long api, int fid, int mid, int lid, int sid);

    public static native Result<List<Book>> getBooksBySerieId(long api, int sid);

    public static class Result<T> {
        private final T value;
        private final String error;
        private final boolean isSuccess;

        private Result(T value, String error, boolean isSuccess) {
            this.value = value;
            this.error = error;
            this.isSuccess = isSuccess;
        }

        public static <T> Result<T> success(T value) {
            return new Result<>(value, null, true);
        }

        public static <T> Result<T> error(String error) {
            return new Result<>(null, error, false);
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public T getValue() {
            return value;
        }

        public String getError() {
            return error;
        }
    }
}
