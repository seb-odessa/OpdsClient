package org.opds.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ErrorReporter {
    Context context;
    final String TAG;
    public ErrorReporter(Context context, String tag) {
        this.context = context;
        this.TAG = tag;
    }

    public void report(String place, String error) {
        String msg = String.format("%s -> %s", place, error);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.e(TAG, msg);
    }
}
