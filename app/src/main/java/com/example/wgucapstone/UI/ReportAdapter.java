package com.example.wgucapstone.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wgucapstone.R;
import com.example.wgucapstone.entities.TripItem;

import java.util.List;

/**
 * Renders a polymorphic list of {@link TripItem} — the same row layout and
 * bind logic works for both vacations and excursions because each subclass
 * overrides getType()/getDateInfo()/getDetail() with its own behavior.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeView;
        private final TextView titleView;
        private final TextView dateView;
        private final TextView detailView;

        ReportViewHolder(View itemView) {
            super(itemView);
            typeView   = itemView.findViewById(R.id.reportType);
            titleView  = itemView.findViewById(R.id.reportTitle);
            dateView   = itemView.findViewById(R.id.reportDate);
            detailView = itemView.findViewById(R.id.reportDetail);
        }
    }

    private List<TripItem> items;
    private final LayoutInflater inflater;

    public ReportAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setItems(List<TripItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.report_list_item, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        TripItem item = items.get(position);
        holder.typeView.setText(item.getType());
        holder.titleView.setText(item.getTitle());
        holder.dateView.setText(item.getDateInfo());
        holder.detailView.setText(item.getDetail());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
