package com.example.d308vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.database.Repository;
import com.example.d308vacationplanner.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {

    private Repository repository;
    private VacationAdapter vacationAdapter;
    private TextView emptyView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Vacations");

        emptyView   = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recyclerview);

        repository      = new Repository(getApplication());
        vacationAdapter = new VacationAdapter(this);

        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FAB → open blank VacationDetails to add a new vacation
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v ->
                startActivity(new Intent(VacationList.this, VacationDetails.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list every time we return here (after add / edit / delete)
        List<Vacation> allVacations = repository.getmAllVacations();
        vacationAdapter.setVacations(allVacations);

        // Show empty state if there are no vacations
        if (allVacations == null || allVacations.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
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