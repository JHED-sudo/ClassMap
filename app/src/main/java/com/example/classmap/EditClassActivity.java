package com.example.classmap;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classmap.database.AppDatabase;
import com.example.classmap.database.Schedule;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class EditClassActivity extends AppCompatActivity {

    private AppDatabase db;
    private Schedule currentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        db = AppDatabase.getInstance(this);

        int id = getIntent().getIntExtra("id", -1);

        EditText subject = findViewById(R.id.inputSubject);
        EditText room = findViewById(R.id.inputRoom);
        EditText time = findViewById(R.id.inputTime);
        Spinner spinnerDay = findViewById(R.id.spinnerDay);

        // Full day names for sorting
        List<String> days = Arrays.asList(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        );
        spinnerDay.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days)
        );

        // Load current schedule by iterating list (original approach)
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Schedule> list = db.scheduleDao().getAll();
            for (Schedule s : list) {
                if (s.id == id) {
                    currentSchedule = s;
                    break;
                }
            }

            if (currentSchedule != null) {
                runOnUiThread(() -> {
                    subject.setText(currentSchedule.subject);
                    room.setText(currentSchedule.room);
                    time.setText(currentSchedule.time);
                    spinnerDay.setSelection(days.indexOf(currentSchedule.day));
                });
            }
        });

        Button btnUpdate = findViewById(R.id.btnSave);
        btnUpdate.setText("Update");

        btnUpdate.setOnClickListener(v -> {
            String subjText = subject.getText().toString().trim();
            String roomText = room.getText().toString().trim();
            String timeText = time.getText().toString().trim();
            String dayText = spinnerDay.getSelectedItem().toString();

            // Validation
            if (subjText.isEmpty() || roomText.isEmpty() || timeText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate time format HH:MM AM/PM
            if (!timeText.matches("^(1[0-2]|0?[1-9]):[0-5][0-9] ?([AaPp][Mm])$")) {
                Toast.makeText(this, "Time format should be HH:MM AM/PM", Toast.LENGTH_SHORT).show();
                return;
            }

            currentSchedule.subject = subjText;
            currentSchedule.room = roomText;
            currentSchedule.time = timeText;
            currentSchedule.day = dayText;

            Executors.newSingleThreadExecutor().execute(() -> {
                db.scheduleDao().update(currentSchedule);
                runOnUiThread(() -> {
                    Toast.makeText(EditClassActivity.this, "Class updated!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });

        // EdgeToEdge padding
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
