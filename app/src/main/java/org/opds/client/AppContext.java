package org.opds.client;

import android.app.Application;
import android.util.Log;

import org.opds.api.jni.Wrapper;

import java.util.concurrent.atomic.AtomicReference;

public class AppContext extends Application {
    private static final String TAG = "org.opds.client.AppContext";
    private static final Wrapper wrapper = new Wrapper();
    private Wrapper.OpdsApi api = null;
    private final AtomicReference<String> libraryPath = new AtomicReference<>();

    public Wrapper.OpdsApi getApi() {
        return api;
    }

    public void setLibraryPath(String path) {
        Log.i(TAG, "setLibraryPath <- " + path);
        libraryPath.set(path);
    }

    public String getLibraryPath() {
        return libraryPath.get();
    }

    public void openDatabase(String uri) {
        Log.i(TAG, "API DB initialization <- " + uri);
        if (null != this.api) {
            api.close();
        }
        this.api = wrapper.create(uri);
    }
}