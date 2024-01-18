package ch.lw.myapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class AddSemesterActivity extends AppCompatActivity {
    EditText input_title;
    Button button_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_semester);

        input_title = findViewById(R.id.input_title);
        button_add = findViewById(R.id.button_add);
        button_add.setOnClickListener(view -> {
            try (DbHelper myDB = new DbHelper(AddSemesterActivity.this)) {
                myDB.addSemester(input_title.getText().toString().trim());
                setResult(1);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}