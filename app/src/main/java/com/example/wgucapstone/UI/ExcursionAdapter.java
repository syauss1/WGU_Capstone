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
import com.example.wgucapstone.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    class ExcursionViewHolder extends RecyclerView.ViewHolder {

        // textView3 and textView4 match the IDs in your existing excursion_list_item.xml
        private final TextView excursionTitleView;
        private final TextView excursionDateView;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionTitleView = itemView.findViewById(R.id.textView3);
            excursionDateView  = itemView.findViewById(R.id.textView4);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final Excursion current = mExcursions.get(position);

                Intent intent = new Intent(context, ExcursionDetails.class);
                intent.putExtra("id",           current.getExcursionID());
                intent.putExtra("title",        current.getTitle());
                intent.putExtra("date",         current.getDate());
                intent.putExtra("vacID",        current.getVacationID());
                intent.putExtra("category",     current.getCategory());
                // Pass vacation date range so ExcursionDetails can validate (B5e)
                intent.putExtra("vacStartDate", mVacStartDate);
                intent.putExtra("vacEndDate",   mVacEndDate);
                context.startActivity(intent);
            });
        }
    }

    private List<Excursion> mExcursions;
    private String mVacStartDate = "";
    private String mVacEndDate   = "";

    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    /**
     * Called from VacationDetails so the adapter knows the parent vacation's
     * date range — forwarded to ExcursionDetails for B5e validation.
     */
    public void setVacationDates(String startDate, String endDate) {
        mVacStartDate = startDate != null ? startDate : "";
        mVacEndDate   = endDate   != null ? endDate   : "";
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            // Bug fix: was setText(int excID) which crashed; show title + date instead
            holder.excursionTitleView.setText(current.getTitle());
            holder.excursionDateView.setText(current.getDate());
        } else {
            holder.excursionTitleView.setText("No excursion title");
            holder.excursionDateView.setText("");
        }
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // Bug fix: guard null before calling .size()
        if (mExcursions != null) return mExcursions.size();
        return 0;
    }
}