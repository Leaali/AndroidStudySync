package ch.lw.myapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomSemesterAdapter extends RecyclerView.Adapter<CustomSemesterAdapter.MyViewHolder> {
    private Context context;
    private ArrayList semester_id, semester_title;

    CustomSemesterAdapter(Context context, ArrayList semester_id, ArrayList semester_title){
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

    }

    @Override
    public int getItemCount() {
        return semester_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView semester_id_text, semester_title_text;
        LinearLayout mainLayout; //TODO: ggf geht das nicht
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            semester_id_text = itemView.findViewById(R.id.semester_id_text);
            semester_title_text = itemView.findViewById(R.id.semester_title_text);
/*            mainLayout = itemView.findViewById(R.id.mainLayout);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);*/
        }
    }
}
