package ch.lw.myapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ch.lw.myapp.adapter.CustomSubjectAdapter;
import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class UpdateSemesterActivity extends AppCompatActivity {
    EditText input_title_edit;
    Button button_update_edit, button_delete_edit;
    String title;
    Integer id;

    //Subject
    RecyclerView view_recycler_subject;
    FloatingActionButton button_add_subject;
    DbHelper HelperDB;
    ArrayList<String> subject_title, subject_grades, subject_average;
    ArrayList<Integer> subject_id, semester_id;
    CustomSubjectAdapter customSubjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_semester);

        input_title_edit = findViewById(R.id.input_title_edit);
        button_update_edit = findViewById(R.id.button_update_edit);
        button_delete_edit = findViewById(R.id.button_delete_edit);

        view_recycler_subject = findViewById(R.id.view_recycler_subject);
        button_add_subject = findViewById(R.id.button_add_subject);

        getAndSetIntentData();
        setupSubjectWithDb();
        button_update_edit.setOnClickListener(view -> updateData());
        button_delete_edit.setOnClickListener(view -> confirmDialog());
        button_add_subject.setOnClickListener(view -> addSubject());
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")) {
            id = getIntent().getIntExtra("id",0);
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
    // ----------------------------------------Subject-------------------------------------------
    void addSubject(){
        Intent intent = new Intent(UpdateSemesterActivity.this, AddSubjectActivity.class);
        intent.putExtra("semester_id",id);
        //TODO check if reload data
        startActivityForResult(intent, 1); //TODO: falls Zeit, versuchen mittels anderen Methode die Einträge nach erstellung zu aktualisieren (recreate)
    }
    void setupSubjectWithDb(){
        HelperDB = new DbHelper(UpdateSemesterActivity.this);
        subject_id = new ArrayList<>();
        semester_id = new ArrayList<>();
        subject_title = new ArrayList<>();
        subject_grades = new ArrayList<>();
        subject_average = new ArrayList<>();

        storeDataInArrays();
        customSubjectAdapter = new CustomSubjectAdapter(UpdateSemesterActivity.this, this, subject_id, semester_id, subject_title, subject_grades, subject_average);
        view_recycler_subject.setAdapter(customSubjectAdapter);
        view_recycler_subject.setLayoutManager(new LinearLayoutManager(UpdateSemesterActivity.this));
    }
    void storeDataInArrays() {
        Cursor cursor = HelperDB.readAllSubjectData(id);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Keine Daten", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                subject_id.add(cursor.getInt(0));
                semester_id.add(cursor.getInt(1));
                subject_title.add(cursor.getString(2));
                subject_grades.add(cursor.getString(3));
                subject_average.add(cursor.getString(4));
            }
        }
    }
    // ----------------------------------------Reload-Subject-------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }
}