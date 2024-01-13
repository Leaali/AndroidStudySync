package ch.lw.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddSemesterActivity extends AppCompatActivity {
    EditText input_title;
    Button button_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_semester);

        input_title = findViewById(R.id.input_title);
        button_add = findViewById(R.id.button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper myDB = new DbHelper(AddSemesterActivity.this);
                myDB.addSemester(input_title.getText().toString().trim());
            }
        });

    }
}