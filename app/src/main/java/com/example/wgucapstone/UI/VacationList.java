package com.example.wgucapstone.UI;

import android.content.Intent;
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
import com.example.wgucapstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {

    private Repository repository;
    private VacationAdapter vacationAdapter;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Vacations");

        emptyView    = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recyclerview);
        searchBox    = findViewById(R.id.searchVacations);

        repository      = new Repository(getApplication());
        vacationAdapter = new VacationAdapter(this);

        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Search functionality — filters the list live as the user types (title or hotel)
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // FAB → open blank VacationDetails to add a new vacation
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v ->
                startActivity(new Intent(VacationList.this, VacationDetails.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list every time we return here (after add / edit / delete),
        // respecting whatever search text is currently in the box.
        refreshList(searchBox.getText().toString());
    }

    private void refreshList(String query) {
        List<Vacation> vacations = query == null || query.trim().isEmpty()
                ? repository.getmAllVacations()
                : repository.searchVacations(query.trim());
        vacationAdapter.setVacations(vacations);

        // Show empty state if there are no matching vacations
        if (vacations == null || vacations.isEmpty()) {
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