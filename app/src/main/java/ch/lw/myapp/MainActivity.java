package ch.lw.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView view_recycler;
    FloatingActionButton button_add;

    DbHelper HelperDB;
    ArrayList<String> semester_id, semester_title;
    CustomSemesterAdapter customSemesterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_recycler = findViewById(R.id.view_recycler);
        button_add = findViewById(R.id.button_add);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSemesterActivity.class);
                startActivity(intent);
            }
        });
        HelperDB = new DbHelper(MainActivity.this);
        semester_id = new ArrayList<>();
        semester_title = new ArrayList<>();

        storeDataInArrays();
        customSemesterAdapter = new CustomSemesterAdapter(MainActivity.this, this, semester_id, semester_title);
        view_recycler.setAdapter(customSemesterAdapter);
        view_recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }
    //refresh page (if. sth. changeg)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }
    void storeDataInArrays() {
        Cursor cursor = HelperDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Keine Daten", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                semester_id.add(cursor.getString(0));
                semester_title.add(cursor.getString(1));
            }
        }
    }
}