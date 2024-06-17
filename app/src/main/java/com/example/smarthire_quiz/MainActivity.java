package com.example.smarthire_quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn;;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_TIME_LEFT = "timeLeftInMillis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn_start);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear any saved quiz state
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(SHARED_PREF_TIME_LEFT);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, QuizPage.class);
                startActivity(intent);
            }
        });


    }
}

