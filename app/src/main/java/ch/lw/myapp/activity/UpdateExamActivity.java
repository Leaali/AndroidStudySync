package ch.lw.myapp.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class UpdateExamActivity extends AppCompatActivity {


    EditText inputExamDate, inputSubject, inputDescription;
    Button buttonUpdate, buttonDelete;

    int id;
    String examDate, subject, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exam);

        inputExamDate = findViewById(R.id.input_exam_date);
        inputSubject = findViewById(R.id.input_subject);
        inputDescription = findViewById(R.id.input_description);
        buttonUpdate = findViewById(R.id.button_update_edit);
        buttonDelete = findViewById(R.id.button_delete_edit);

        getAndSetIntentData();

        buttonUpdate.setOnClickListener(view -> updateData());
        buttonDelete.setOnClickListener(view -> confirmDialog(id));
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id")) {
            id = getIntent().getIntExtra("id",0);
            examDate = getIntent().getStringExtra("date");
            subject = getIntent().getStringExtra("subject");
            description = getIntent().getStringExtra("description");

            inputExamDate.setText(examDate);
            inputSubject.setText(subject);
            inputDescription.setText(description);

            Log.d("stev", examDate);
        } else {
            Toast.makeText(this, "Keine Daten.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateData() {
        String newExamDate = inputExamDate.getText().toString().trim();
        String newSubject = inputSubject.getText().toString().trim();
        String newDescription = inputDescription.getText().toString().trim();

        if (!newExamDate.isEmpty() && !newSubject.isEmpty() && !newDescription.isEmpty()) {
            new DbHelper(UpdateExamActivity.this).updateExam(id, newExamDate, newSubject, newDescription);
        } else {
            Toast.makeText(this, "Bitte fülle alle Felder aus.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(int examId) {
        new AlertDialog.Builder(this)
                .setTitle("Prüfung löschen?")
                .setMessage("Bist du dir sicher, dass du diese Prüfung löschen möchtest?")
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    new DbHelper(UpdateExamActivity.this).deleteExam(examId);
                    finish();
                })
                .setNegativeButton("Nein", null)
                .show();
    }
}