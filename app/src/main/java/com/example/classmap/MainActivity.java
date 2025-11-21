package com.example.classmap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import com.example.classmap.database.AppDatabase;
import com.example.classmap.database.Schedule;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private LinearLayout tableContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        tableContainer = findViewById(R.id.tableContainer);

        findViewById(R.id.btnAdd).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, add_class.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }

    private void loadSchedules() {
        Executors.newSingleThreadExecutor().execute(() -> {

            List<Schedule> list = db.scheduleDao().getAll();

            // Sort schedules by day and time
            list.sort((a, b) -> {
                int dayCompare = Integer.compare(getDayOrder(a.day), getDayOrder(b.day));
                if (dayCompare != 0) return dayCompare;
                return Integer.compare(parseTimeToMinutes(a.time), parseTimeToMinutes(b.time));
            });

            runOnUiThread(() -> {

                tableContainer.removeAllViews();

                String currentDay = "";

                for (Schedule s : list) {

                    // Section header (Day)
                    if (!s.day.equalsIgnoreCase(currentDay)) {
                        currentDay = s.day;

                        Button dayHeader = new Button(MainActivity.this);
                        dayHeader.setText("  " + currentDay + "  ");
                        dayHeader.setBackgroundColor(Color.parseColor("#333333"));
                        dayHeader.setTextColor(Color.WHITE);
                        dayHeader.setAllCaps(true);
                        dayHeader.setTextSize(18);
                        dayHeader.setPadding(20, 15, 20, 15);

                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                        params.setMargins(0, 30, 0, 10);
                        dayHeader.setLayoutParams(params);

                        tableContainer.addView(dayHeader);
                    }

                    // Schedule button
                    Button btn = new Button(MainActivity.this);
                    btn.setText(s.time + "\n" + s.subject + "\n" + s.room);

                    btn.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_add));
                    btn.setTextColor(Color.WHITE);
                    btn.setTextSize(16);
                    btn.setPadding(25, 25, 25, 25);
                    btn.setElevation(10);

                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 5, 0, 10);
                    btn.setLayoutParams(params);

                    // OPEN MAP
                    btn.setOnClickListener(v -> {
                        Intent i = new Intent(MainActivity.this, map.class);
                        i.putExtra("room", s.room.toLowerCase()); // lowercase for drawable
                        startActivity(i);
                    });

                    // EDIT / DELETE
                    btn.setOnLongClickListener(v -> {
                        showOptionsDialog(s);
                        return true;
                    });

                    tableContainer.addView(btn);
                }
            });
        });
    }

    private void showOptionsDialog(Schedule s) {
        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit
                        Intent i = new Intent(MainActivity.this, EditClassActivity.class);
                        i.putExtra("id", s.id);
                        startActivity(i);
                    } else {
                        // Delete
                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.scheduleDao().delete(s);
                            runOnUiThread(this::loadSchedules);
                        });
                    }
                }).show();
    }

    // Convert day name to order
    private int getDayOrder(String day) {
        switch (day.toLowerCase()) {
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            case "sunday": return 7;
        }
        return 999;
    }

    // Convert time string "7:30 AM" → minutes
    private int parseTimeToMinutes(String time) {
        if (time == null) return 0;

        try {
            time = time.trim().toUpperCase();
            String[] parts = time.split(" ");
            String[] hm = parts[0].split(":");
            int hour = Integer.parseInt(hm[0]);
            int minute = hm.length > 1 ? Integer.parseInt(hm[1]) : 0;

            boolean isPM = parts.length > 1 && parts[1].equals("PM");
            if (hour == 12) hour = 0;
            if (isPM) hour += 12;

            return hour * 60 + minute;
        } catch (Exception e) {
            return 0;
        }
    }
}
