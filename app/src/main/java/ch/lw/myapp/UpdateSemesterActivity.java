package ch.lw.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateSemesterActivity extends AppCompatActivity {
    EditText input_title_edit;
    Button  button_update_edit;
    String id, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_semester);

        input_title_edit = findViewById(R.id.input_title_edit);
        button_update_edit = findViewById(R.id.button_update_edit);

        getAndSetIntentData();

        button_update_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper helperDB = new DbHelper(UpdateSemesterActivity.this);
                title = input_title_edit.getText().toString().trim();
                helperDB.updateData(id, title);
            }
        });

    }
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("title")){
            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");

            //Setting Intent Data
            input_title_edit.setText(title);
            Log.d("stev", title);
        }else{
            Toast.makeText(this, "Keine Daten.", Toast.LENGTH_SHORT).show();
        }
    }
}