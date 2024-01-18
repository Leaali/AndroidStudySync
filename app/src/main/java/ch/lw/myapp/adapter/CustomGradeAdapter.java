package ch.lw.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.lw.myapp.R;
import ch.lw.myapp.activity.UpdateSubjectActivity;
import ch.lw.myapp.db.DbHelper;

public class CustomGradeAdapter extends RecyclerView.Adapter<CustomGradeAdapter.MyViewHolder> {
    private final DbHelper dbHelper;
    private final Context context;
    private final ArrayList<String> grade_title;
    private final ArrayList<Integer> grade_id, subject_id;
    private final ArrayList<Double> grade_weight, grade_value;

    public CustomGradeAdapter(Context context, DbHelper dbHelper, ArrayList<Integer> grade_id, ArrayList<Integer> subject_id, ArrayList<String> grade_title, ArrayList<Double> grade_weight, ArrayList<Double> grade_value) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.grade_id = grade_id;
        this.subject_id = subject_id;
        this.grade_title = grade_title;
        this.grade_weight = grade_weight;
        this.grade_value = grade_value;
    }

    @NonNull
    @Override
    public CustomGradeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_grade, parent, false);
        return new CustomGradeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomGradeAdapter.MyViewHolder holder, int position) {
        holder.input_title_grade_edit.setText(grade_title.get(position));
        holder.input_weight_grade_edit.setText(String.valueOf(grade_weight.get(position)));
        holder.input_grade_grade_edit.setText(String.valueOf(grade_value.get(position)));
        holder.button_save_grade.setOnClickListener(view -> saveChanges(position, holder.input_title_grade_edit.getText().toString(), Double.parseDouble(holder.input_weight_grade_edit.getText().toString()), Double.parseDouble(holder.input_grade_grade_edit.getText().toString())));

/*      // Speichern beim verlassen des Input Feldes => führte öfters zu chrashes
        holder.input_title_grade_edit.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                saveChanges(position, holder.input_title_grade_edit.getText().toString(), Double.parseDouble(holder.input_weight_grade_edit.getText().toString()), Double.parseDouble(holder.input_grade_grade_edit.getText().toString()));
            }
        });
        holder.input_weight_grade_edit.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                saveChanges(position, holder.input_title_grade_edit.getText().toString(), Double.parseDouble(holder.input_weight_grade_edit.getText().toString()), Double.parseDouble(holder.input_grade_grade_edit.getText().toString()));
            }
        });
        holder.input_grade_grade_edit.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                saveChanges(position, holder.input_title_grade_edit.getText().toString(), Double.parseDouble(holder.input_weight_grade_edit.getText().toString()), Double.parseDouble(holder.input_grade_grade_edit.getText().toString()));
            }
        });*/

        holder.button_delete_grade.setOnClickListener(view -> deleteGrade(position));
    }

    @Override
    public int getItemCount() {
        return grade_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        EditText input_title_grade_edit, input_weight_grade_edit, input_grade_grade_edit;
        Button button_delete_grade, button_save_grade;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            input_title_grade_edit = itemView.findViewById(R.id.input_title_grade_edit);
            input_weight_grade_edit = itemView.findViewById(R.id.input_weight_grade_edit);
            input_grade_grade_edit = itemView.findViewById(R.id.input_grade_grade_edit);
            button_delete_grade = itemView.findViewById(R.id.button_delete_grade);
            button_save_grade= itemView.findViewById(R.id.button_save_grade);
        }
    }

    private void saveChanges(int position, String newTitle, Double newWeight, Double newGrade) {
        int gradeId = grade_id.get(position);
        dbHelper.updateGrade(gradeId, newTitle, newWeight, newGrade);
        // Aktualisierung von den werten, um den Average zu berechnen
        grade_title.set(position, newTitle);
        grade_weight.set(position, newWeight);
        grade_value.set(position, newGrade);
        //notifyItemChanged(position);
        ((UpdateSubjectActivity) context).calculateAndDisplayAverage();
    }

    private void deleteGrade(int position) {
        int gradeId = grade_id.get(position);

        dbHelper.deleteGrade(gradeId);

        grade_id.remove(position);
        subject_id.remove(position);
        grade_title.remove(position);
        grade_weight.remove(position);
        grade_value.remove(position);

        //notifyItemRemoved(position); // Aktualisierung von den werten, um den Average zu berechnen
        notifyItemRangeChanged(position, getItemCount());
        ((UpdateSubjectActivity) context).calculateAndDisplayAverage();
        Toast.makeText(context, "Note erfolgreich gelöscht!", Toast.LENGTH_SHORT).show();
    }
}