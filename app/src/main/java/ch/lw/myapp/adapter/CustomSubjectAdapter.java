package ch.lw.myapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.lw.myapp.R;
import ch.lw.myapp.activity.UpdateSubjectActivity;

public class CustomSubjectAdapter extends RecyclerView.Adapter<CustomSubjectAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<String> subject_title, subject_grades, subject_average;
    private final ArrayList<Integer> subject_id, semester_id;
    Activity activity;

    public CustomSubjectAdapter(Activity activity, Context context, ArrayList<Integer> subject_id, ArrayList<Integer> semester_id, ArrayList<String> subject_title, ArrayList<String> subject_grades, ArrayList<String> subject_average) {
        this.activity = activity;
        this.context = context;
        this.subject_id = subject_id;
        this.semester_id = semester_id;
        this.subject_title = subject_title;
        this.subject_grades = subject_grades;
        this.subject_average = subject_average;
    }

    @NonNull
    @Override
    public CustomSubjectAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_subject, parent, false);
        return new CustomSubjectAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomSubjectAdapter.MyViewHolder holder, int position) {
        //get String from the Array
        holder.subject_id_text.setText(String.valueOf(subject_id.get(position)));
        holder.subject_title_text.setText(String.valueOf(subject_title.get(position)));
        holder.subject_grades_text.setText(String.valueOf(subject_grades.get(position)));
        holder.subject_average_text.setText(String.valueOf(subject_average.get(position)));

        holder.mainLayoutSubject.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, UpdateSubjectActivity.class);
                intent.putExtra("id", String.valueOf(subject_id.get(adapterPosition)));
                intent.putExtra("semester", String.valueOf(semester_id.get(adapterPosition)));
                intent.putExtra("title", String.valueOf(subject_title.get(adapterPosition)));
                intent.putExtra("grades", String.valueOf(subject_grades.get(adapterPosition)));
                intent.putExtra("average", String.valueOf(subject_average.get(adapterPosition)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subject_id.size(); //TODO: prüfen ob das korrekt ist, sonst noch where semester = semester_id
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subject_id_text, subject_title_text, subject_grades_text, subject_average_text;
        ConstraintLayout mainLayoutSubject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subject_id_text = itemView.findViewById(R.id.subject_id_text);
            subject_title_text = itemView.findViewById(R.id.subject_title_text);
            subject_grades_text = itemView.findViewById(R.id.subject_grades_text);
            subject_average_text = itemView.findViewById(R.id.subject_average_text);
            mainLayoutSubject = itemView.findViewById(R.id.mainLayoutSubject);
        }
    }
}
