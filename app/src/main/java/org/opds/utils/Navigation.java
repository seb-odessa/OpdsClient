package org.opds.utils;


import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.client.MainActivity;
import org.opds.client.R;

public class Navigation {
    public static void create(AppCompatActivity activity) {
        Button buttonHome = activity.findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        });

        Button buttonBack = activity.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            activity.finish();
        });
    }
}
