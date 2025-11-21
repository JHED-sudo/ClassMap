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

public class add_class extends AppCompatActivity {
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        db = AppDatabase.getInstance(this);

        EditText subject = findViewById(R.id.inputSubject);
        EditText room = findViewById(R.id.inputRoom);
        EditText time = findViewById(R.id.inputTime);
        Spinner spinnerDay = findViewById(R.id.spinnerDay);

        // Full day names for sorting compatibility
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        spinnerDay.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, days));

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {

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

            Schedule s = new Schedule(subjText, roomText, dayText, timeText);

            Executors.newSingleThreadExecutor().execute(() -> {
                db.scheduleDao().insert(s);
                runOnUiThread(() -> {
                    Toast.makeText(add_class.this, "Class added!", Toast.LENGTH_SHORT).show();
                    finish(); // return to MainActivity
                });
            });
        });

        /*
        // Optional: Cancel button (if added in layout)
        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
        */

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
