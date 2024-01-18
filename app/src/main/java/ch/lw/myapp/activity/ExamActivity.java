package ch.lw.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import ch.lw.myapp.R;

public class ExamActivity extends AppCompatActivity {
    //Navigation - Drawer from: https://www.geeksforgeeks.org/navigation-drawer-in-android/
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        prepareNavigationDrawer();
    }
    void prepareNavigationDrawer(){
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