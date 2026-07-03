package com.example.wgucapstone.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wgucapstone.R;
import com.example.wgucapstone.entities.Vacation;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    class VacationViewHolder extends RecyclerView.ViewHolder {

        // textView2 matches the ID in your existing vacation_list_item.xml
        private final TextView vacationItemView;
        private final TextView vacationDatesView;

        private VacationViewHolder(View itemView) {
            super(itemView);
            vacationItemView  = itemView.findViewById(R.id.textView2);
            vacationDatesView = itemView.findViewById(R.id.textViewDates);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final Vacation current = mVacations.get(position);

                Intent intent = new Intent(context, VacationDetails.class);
                intent.putExtra("id",        current.getVacationID());
                intent.putExtra("title",     current.getTitle());
                intent.putExtra("hotel",     current.getHotel());
                intent.putExtra("startDate", current.getStartDate());
                intent.putExtra("endDate",   current.getEndDate());
                context.startActivity(intent);
            });
        }
    }

    private List<Vacation> mVacations;
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (mVacations != null) {
            Vacation current = mVacations.get(position);
            holder.vacationItemView.setText(current.getTitle());
            holder.vacationDatesView.setText(
                    current.getStartDate() + " – " + current.getEndDate());
        } else {
            holder.vacationItemView.setText("No vacation name");
            holder.vacationDatesView.setText("");
        }
    }

    public void setVacations(List<Vacation> vacations) {
        mVacations = vacations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // Bug fix: guard null before calling .size()
        if (mVacations != null) return mVacations.size();
        return 0;
    }
}