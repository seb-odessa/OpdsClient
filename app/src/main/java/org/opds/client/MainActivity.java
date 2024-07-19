package org.opds.client;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.opds.api.jni.Wrapper;
import org.opds.utils.FileUtils;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.client.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
    private static final org.opds.api.jni.Wrapper wrapper;

    static {
//        System.loadLibrary("opds_jni");
        wrapper = new Wrapper();

    }

    private org.opds.client.databinding.ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String dbPath = FileUtils.copyAssetToInternalStorage(this, "books.db");
        String uri = String.format("file:%s?mode=ro", dbPath);
        Wrapper.OpdsApi api = wrapper.create(uri);

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(getString(R.string.app_name));
    }


}