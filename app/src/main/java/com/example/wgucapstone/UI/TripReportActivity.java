package com.example.wgucapstone.UI;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wgucapstone.R;
import com.example.wgucapstone.database.Repository;
import com.example.wgucapstone.entities.Excursion;
import com.example.wgucapstone.entities.ExcursionTripItem;
import com.example.wgucapstone.entities.TripItem;
import com.example.wgucapstone.entities.Vacation;
import com.example.wgucapstone.entities.VacationTripItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Combined search + report screen. Builds one polymorphic list of
 * {@link TripItem} out of every Vacation and Excursion, optionally filtered
 * by a keyword, and renders it as a multi-column, multi-row report with a
 * title and a generation timestamp.
 */
public class TripReportActivity extends AppCompatActivity {

    private Repository repository;
    private ReportAdapter reportAdapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView generatedAtView;

    private static final SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_report);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trip Report");

        repository      = new Repository(getApplication());
        reportAdapter   = new ReportAdapter(this);
        recyclerView    = findViewById(R.id.reportRecyclerView);
        emptyView       = findViewById(R.id.reportEmptyView);
        generatedAtView = findViewById(R.id.reportGeneratedAt);

        recyclerView.setAdapter(reportAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText searchBox = findViewById(R.id.reportSearch);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshReport(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshReport("");
    }

    private void refreshReport(String query) {
        List<TripItem> items = buildTripItems(query == null ? "" : query.trim().toLowerCase(Locale.US));
        reportAdapter.setItems(items);

        generatedAtView.setText("Generated: " + TIMESTAMP_FORMAT.format(new Date()));

        if (items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private List<TripItem> buildTripItems(String query) {
        List<Vacation> vacations = repository.getmAllVacations();
        List<Excursion> excursions = repository.getAllExcursions();

        Map<Integer, String> vacationTitleById = new HashMap<>();
        for (Vacation v : vacations) {
            vacationTitleById.put(v.getVacationID(), v.getTitle());
        }

        List<TripItem> items = new ArrayList<>();

        for (Vacation v : vacations) {
            TripItem item = new VacationTripItem(v);
            if (matches(item, query)) items.add(item);
        }
        for (Excursion e : excursions) {
            String vacationTitle = vacationTitleById.get(e.getVacationID());
            TripItem item = new ExcursionTripItem(e, vacationTitle != null ? vacationTitle : "Unknown");
            if (matches(item, query)) items.add(item);
        }

        return items;
    }

    private boolean matches(TripItem item, String query) {
        if (query.isEmpty()) return true;
        return item.getTitle().toLowerCase(Locale.US).contains(query)
                || item.getDetail().toLowerCase(Locale.US).contains(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
