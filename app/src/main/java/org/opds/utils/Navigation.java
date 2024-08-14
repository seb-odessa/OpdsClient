package org.opds.utils;


import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.opds.client.MainActivity;
import org.opds.client.R;

public class Navigation {

    public interface AfterTextChanged {
        void afterTextChanged(Editable editable);
    }

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

    public static void afterTextChanged(EditText editText, AfterTextChanged action) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                action.afterTextChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }
        });
    }
}
