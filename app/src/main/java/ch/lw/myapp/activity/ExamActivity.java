package ch.lw.myapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ch.lw.myapp.R;
import ch.lw.myapp.adapter.CustomExamAdapter;
import ch.lw.myapp.db.DbHelper;

public class ExamActivity extends AppCompatActivity {
    RecyclerView view_recycler;
    FloatingActionButton button_add;

    DbHelper HelperDB;
    ArrayList<String> exam_description, exam_subject, exam_date;
    ArrayList<Integer> exam_id;
    CustomExamAdapter customExamAdapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        prepareNavigationDrawer();
        view_recycler = findViewById(R.id.view_recycler);
        button_add = findViewById(R.id.button_add);
        button_add.setOnClickListener(view -> {
            Intent intent = new Intent(ExamActivity.this, AddExamActivity.class);
            startActivityForResult(intent, 1); //TODO: falls Zeit, versuchen mittels anderen Methode die Einträge nach erstellung zu aktualisieren (recreate)
        });
        prepareDataBase();
    }

    void prepareDataBase() {
        HelperDB = new DbHelper(ExamActivity.this);
        exam_id = new ArrayList<>();
        exam_date = new ArrayList<>();
        exam_description = new ArrayList<>();
        exam_subject = new ArrayList<>();
        Cursor cursor = HelperDB.readAllExam();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Keine Daten", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                exam_id.add(cursor.getInt(0));
                exam_date.add(cursor.getString(1));
                exam_description.add(cursor.getString(2));
                exam_subject.add(cursor.getString(3));
            }
        }

        customExamAdapter = new CustomExamAdapter(ExamActivity.this, this, exam_id, exam_date, exam_description, exam_subject);
        view_recycler.setAdapter(customExamAdapter);
        view_recycler.setLayoutManager(new LinearLayoutManager(ExamActivity.this));
    }

    // Refresh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    //----------------------------------------Navigation-Drawer-------------------------------------------
    // Navigation - Drawer from: https://www.geeksforgeeks.org/navigation-drawer-in-android/
    void prepareNavigationDrawer() {
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START); //close drawer
            return true;
        });
    }

    // Drawer Code from https://www.geeksforgeeks.org/navigation-drawer-in-android/
    // ovverride click listener callback to open and close the navigation drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectDrawerItem(int itemId) {
        Intent intent = null;
        if (itemId == R.id.grade_overview) {
            intent = new Intent(ExamActivity.this, MainActivity.class);
        } else if (itemId == R.id.upcoming_tests) {
            intent = new Intent(ExamActivity.this, ExamActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}