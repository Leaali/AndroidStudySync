package ch.lw.myapp.activity;

import android.app.NotificationManager;
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
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ch.lw.myapp.service.ExamNotificationService2;
import ch.lw.myapp.adapter.CustomSemesterAdapter;
import ch.lw.myapp.R;
import ch.lw.myapp.db.DbHelper;

public class MainActivity extends AppCompatActivity {
    private NotificationManager notificationManager;

    RecyclerView view_recycler;
    FloatingActionButton button_add;

    DbHelper HelperDB;
    ArrayList<String> semester_id, semester_title;
    CustomSemesterAdapter customSemesterAdapter;

    //Navigation - Drawer from: https://www.geeksforgeeks.org/navigation-drawer-in-android/
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_recycler = findViewById(R.id.view_recycler);
        button_add = findViewById(R.id.button_add);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        button_add.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddSemesterActivity.class);
            startActivityForResult(intent, 1); //TODO: falls Zeit, versuchen mittels anderen Methode die Einträge nach erstellung zu aktualisieren (recreate)
        });
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        HelperDB = new DbHelper(MainActivity.this);
        semester_id = new ArrayList<>();
        semester_title = new ArrayList<>();

        storeDataInArrays();
        customSemesterAdapter = new CustomSemesterAdapter(MainActivity.this, this, semester_id, semester_title);
        view_recycler.setAdapter(customSemesterAdapter);
        view_recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        setupNavigationView();

        Intent serviceIntent = new Intent(this, ExamNotificationService2.class);
        startService(serviceIntent);
    }

    //refresh page (if. sth. changeg)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = HelperDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Keine Daten", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                semester_id.add(cursor.getString(0));
                semester_title.add(cursor.getString(1));
            }
        }
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

    // code From: https://www.tabnine.com/code/java/methods/com.google.android.material.navigation.NavigationView/setNavigationItemSelectedListener?snippet=5ce69ad17e03440004d0f4d3
    private void setupNavigationView() {
        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START); //close drawer
            return true;
        });
    }

    private void selectDrawerItem(int itemId) {
        Intent intent = null;

        if (itemId == R.id.grade_overview) {
            intent = new Intent(MainActivity.this, MainActivity.class);
        } else if (itemId == R.id.upcoming_tests) {
            //TODO: change second MainActivity to UpcomingTestActivity
            intent = new Intent(MainActivity.this, ExamActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
    //---------- Notivication Service
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "defaultChannel")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Snow")
                .setContentText("It's snowing!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
    }
}