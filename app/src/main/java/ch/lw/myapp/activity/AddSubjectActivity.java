package ch.lw.myapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class AddSubjectActivity extends AppCompatActivity {
    EditText input_title_subject;
    Button button_add_subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        String semesterIdString = getIntent().getStringExtra("semester_id");
        long semesterId = Long.parseLong(semesterIdString);

        input_title_subject = findViewById(R.id.input_title_subject);
        button_add_subject = findViewById(R.id.button_add_subject);
        button_add_subject.setOnClickListener(view -> {
            DbHelper myDB = new DbHelper(AddSubjectActivity.this);
            myDB.addSubject(semesterId, input_title_subject.getText().toString().trim());
            setResult(1);
            finish();
        });
    }
}