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
import ch.lw.myapp.activity.UpdateExamActivity;

public class CustomExamAdapter extends RecyclerView.Adapter<CustomExamAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<Integer> exam_id;
    private final ArrayList<String> exam_description, exam_subject, exam_date;
    Activity activity;

    public CustomExamAdapter(Activity activity, Context context, ArrayList<Integer> exam_id, ArrayList<String> exam_date, ArrayList<String> exam_description, ArrayList<String> exam_subject) {
        this.activity = activity;
        this.context = context;
        this.exam_id = exam_id;
        this.exam_date = exam_date;
        this.exam_description = exam_description;
        this.exam_subject = exam_subject;
    }

    @NonNull
    @Override
    public CustomExamAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_exam, parent, false);
        return new CustomExamAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomExamAdapter.MyViewHolder holder, int position) {
        holder.exam_date_text.setText(exam_date.get(position));
        holder.exam_description_text.setText(exam_description.get(position));
        holder.exam_subject_text.setText(exam_subject.get(position));

        holder.mainLayout.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, UpdateExamActivity.class);
                intent.putExtra("id", String.valueOf(exam_id.get(adapterPosition)));
                intent.putExtra("date", exam_date.get(adapterPosition));
                intent.putExtra("description", exam_description.get(adapterPosition));
                intent.putExtra("subject", exam_subject.get(adapterPosition));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exam_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView exam_date_text, exam_description_text, exam_subject_text;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exam_date_text = itemView.findViewById(R.id.input_exam_date);
            exam_description_text = itemView.findViewById(R.id.input_description);
            exam_subject_text = itemView.findViewById(R.id.input_subject);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
