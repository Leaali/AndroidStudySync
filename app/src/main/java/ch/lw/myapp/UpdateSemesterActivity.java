package ch.lw.myapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateSemesterActivity extends AppCompatActivity {
    EditText input_title_edit;
    Button  button_update_edit, button_delete_edit;
    String id, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_semester);

        input_title_edit = findViewById(R.id.input_title_edit);
        button_update_edit = findViewById(R.id.button_update_edit);
        button_delete_edit = findViewById(R.id.button_delete_edit);

        getAndSetIntentData();

        button_update_edit.setOnClickListener(view -> updateData());
        button_delete_edit.setOnClickListener(view -> confirmDialog());
    }
    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            input_title_edit.setText(title);
            Log.d("stev", title);
        } else {
            Toast.makeText(this, "Keine Daten.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateData() {
        String newTitle = input_title_edit.getText().toString().trim();
        if (!newTitle.isEmpty()) {
            new DbHelper(UpdateSemesterActivity.this).updateData(id, newTitle);
        } else {
            Toast.makeText(this, "Titel darf nicht leer sein.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Semester " + title + " löschen?")
                .setMessage("Bist du dir sicher, dass du das Semester " + title + " löschen möchtest?")
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    new DbHelper(UpdateSemesterActivity.this).deleteOneRow(id);
                    finish();
                })
                .setNegativeButton("Nein", null)
                .show();
    }

}