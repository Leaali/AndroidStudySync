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
import ch.lw.myapp.activity.UpdateSemesterActivity;

public class CustomSemesterAdapter extends RecyclerView.Adapter<CustomSemesterAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<String> semester_title;
    private final ArrayList<Integer> semester_id;
    Activity activity;

    public CustomSemesterAdapter(Activity activity, Context context, ArrayList<Integer> semester_id, ArrayList<String> semester_title) {
        this.activity = activity;
        this.context = context;
        this.semester_id = semester_id;
        this.semester_title = semester_title;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_semester, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //get String from the Array
        holder.semester_id_text.setText(String.valueOf(semester_id.get(position)));
        holder.semester_title_text.setText(String.valueOf(semester_title.get(position)));

        holder.mainLayout.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, UpdateSemesterActivity.class);
                intent.putExtra("id", semester_id.get(adapterPosition));
                intent.putExtra("title", String.valueOf(semester_title.get(adapterPosition)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return semester_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView semester_id_text, semester_title_text;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            semester_id_text= itemView.findViewById(R.id.semester_id_text);
            semester_title_text = itemView.findViewById(R.id.semester_title_text);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
