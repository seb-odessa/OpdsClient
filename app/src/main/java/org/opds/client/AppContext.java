package org.opds.client;

import android.app.Application;

import org.opds.api.jni.Wrapper;

public class AppContext extends Application {

    private static final Wrapper wrapper = new Wrapper();
    private Wrapper.OpdsApi api = null;

    public Wrapper.OpdsApi getApi() {
        return api;
    }

    public void reset(String uri) {
        if (null != this.api) {
            api.close();
        }
        this.api = wrapper.create(uri);
    }
}