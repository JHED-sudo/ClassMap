package com.example.classmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loadingscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreen);

        Intent intent = new Intent(loadingscreen.this,MainActivity.class);

        Handler handler = new Handler(Looper.getMainLooper()); // Or specify another Looper

        long delayMillis;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Code to execute after the specified delay
                startActivity(intent);
                finish();
            }
        }, 5000); // Delay in milliseconds

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}