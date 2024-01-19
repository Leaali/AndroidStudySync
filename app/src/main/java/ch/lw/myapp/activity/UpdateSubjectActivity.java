package ch.lw.myapp.activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ch.lw.myapp.adapter.CustomGradeAdapter;
import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class UpdateSubjectActivity extends AppCompatActivity {
    EditText input_title_subject_edit;
    Button button_update_subject_edit, button_delete_subject_edit;
    FloatingActionButton button_add_grade;
    String title;
    int id;

    // ---Grade---
    CustomGradeAdapter customGradeAdapter;
    ArrayList<String> grade_title;
    ArrayList<Integer> grade_id, subject_id;
    ArrayList<Double> grade_weight, grade_value;
    RecyclerView view_recycler_grade;
    TextView text_grade_average;
    DbHelper helperDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_subject);

        input_title_subject_edit = findViewById(R.id.input_title_subject_edit);
        button_update_subject_edit = findViewById(R.id.button_update_subject_edit);
        button_delete_subject_edit = findViewById(R.id.button_delete_subject_edit);
        button_add_grade = findViewById(R.id.button_add_grade);
        view_recycler_grade = findViewById(R.id.view_recycler_grade);
        text_grade_average = findViewById(R.id.text_grade_average);

        getAndSetIntentData();
        setupGradeWithDb();
        loadGrades(id);
        button_update_subject_edit.setOnClickListener(view -> updateData());
        button_delete_subject_edit.setOnClickListener(view -> confirmDialog());
        button_add_grade.setOnClickListener(view -> addGrade());
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")) {
            id = getIntent().getIntExtra("id",0);
            title = getIntent().getStringExtra("title");
            input_title_subject_edit.setText(title);
            Log.d("stev", title);
        } else {
            Toast.makeText(this, "Keine Daten.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateData() {
        String newTitle = input_title_subject_edit.getText().toString().trim();
        if (!newTitle.isEmpty()) {
            new DbHelper(UpdateSubjectActivity.this).updateSubject(id, newTitle);
        } else {
            Toast.makeText(this, "Titel darf nicht leer sein.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Fach " + title + " löschen?")
                .setMessage("Bist du dir sicher, dass du das Fach " + title + " löschen möchtest?")
                .setPositiveButton("Ja", (dialogInterface, i) -> {
                    new DbHelper(UpdateSubjectActivity.this).deleteSubject(id);
                    finish();
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    // ----------------------------------------Grade-------------------------------------------
    void setupGradeWithDb() {
        helperDB = new DbHelper(UpdateSubjectActivity.this);
        grade_id = new ArrayList<>();
        subject_id = new ArrayList<>();
        grade_title = new ArrayList<>();
        grade_weight = new ArrayList<>();
        grade_value = new ArrayList<>();

        customGradeAdapter = new CustomGradeAdapter(UpdateSubjectActivity.this, helperDB, grade_id, subject_id, grade_title, grade_weight, grade_value);
        view_recycler_grade.setAdapter(customGradeAdapter);
        view_recycler_grade.setLayoutManager(new LinearLayoutManager(UpdateSubjectActivity.this));
    }

    void addGrade() {
        String gradeTitle = "";
        double gradeWeight = 1.0;
        double gradeValue = 0.0;
        @SuppressLint("ResourceType") Animation bounceAnimation = AnimationUtils.loadAnimation(UpdateSubjectActivity.this, R.drawable.bounce);
        button_add_grade.startAnimation(bounceAnimation);

        long result = helperDB.addGrade(id, gradeTitle, gradeWeight, gradeValue);

        if (result != -1) {
            Toast.makeText(this, "Note erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
            loadGrades(id); // Reload Recycle view
        } else {
            Toast.makeText(this, "Fehler beim Hinzufügen der Note.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadGrades(int subjectId) {
        Cursor cursor = helperDB.readAllGrades(subjectId);

        grade_id.clear();
        subject_id.clear();
        grade_title.clear();
        grade_weight.clear();
        grade_value.clear();

        int columnIndexId = cursor.getColumnIndex(DbHelper.COLUMN_GRADE_ID);
        int columnIndexSubjectId = cursor.getColumnIndex(DbHelper.COLUMN_SUBJECT_ID_GRADE);
        int columnIndexTitle = cursor.getColumnIndex(DbHelper.COLUMN_GRADE_TITLE);
        int columnIndexWeight = cursor.getColumnIndex(DbHelper.COLUMN_GRADE_WEIGHTING);
        int columnIndexValue = cursor.getColumnIndex(DbHelper.COLUMN_GRADE_VALUE);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                grade_id.add(cursor.getInt(columnIndexId));
                subject_id.add(cursor.getInt(columnIndexSubjectId));
                grade_title.add(cursor.getString(columnIndexTitle));
                grade_weight.add(cursor.getDouble(columnIndexWeight));
                grade_value.add(cursor.getDouble(columnIndexValue));
            }
            customGradeAdapter.notifyDataSetChanged(); // Aktualisiere den RecyclerView-Adapter
            calculateAndDisplayAverage();
        }
        cursor.close();
    }

    public void calculateAndDisplayAverage() {
        double totalWeightedValue = 0.0;
        double totalWeight = 0.0;

        for (int i = 0; i < grade_id.size(); i++) {
            double weight = grade_weight.get(i);
            double value = grade_value.get(i);
            totalWeightedValue += weight * value;
            totalWeight += weight;
        }

        double currentAverage  = totalWeight > 0 ? totalWeightedValue / totalWeight : 0.0;
        helperDB.updateSubjectAverage(subject_id.get(0), currentAverage);
        text_grade_average.setText(String.valueOf(currentAverage));

    }
}