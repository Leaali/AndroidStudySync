package ch.lw.myapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class AddExamActivity extends AppCompatActivity {
    private EditText input_exam_date, input_subject, input_description;
    private Button button_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        input_exam_date = findViewById(R.id.input_exam_date);
        input_subject = findViewById(R.id.input_subject);
        input_description = findViewById(R.id.input_description);
        button_add = findViewById(R.id.button_add);

        datePicker();
        button_add.setOnClickListener(view -> {
            try (DbHelper myDB = new DbHelper(AddExamActivity.this)) {
                myDB.addExam(
                        input_exam_date.getText().toString().trim(),
                        input_subject.getText().toString().trim(),
                        input_description.getText().toString().trim());
                setResult(1);
                finish();
            } catch (Exception e) {
                setResult(1);
                e.printStackTrace();
                finish();
            }
        });

    }

    void datePicker() {
        // Dialog
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Datum wählen")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        input_exam_date.setOnClickListener(view -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        //Update datepicker
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = dateFormat.format(selection);
            input_exam_date.setText(formattedDate);
        });
    }
}