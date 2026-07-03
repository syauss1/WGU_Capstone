package com.example.wgucapstone.UI;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgucapstone.R;
import com.example.wgucapstone.database.Repository;
import com.example.wgucapstone.entities.Category;
import com.example.wgucapstone.entities.Excursion;
import com.example.wgucapstone.receivers.AlarmReceiver;
import com.example.wgucapstone.util.DateRangeValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────────────────
    private EditText editTitle;
    private EditText editDate;
    private Spinner categorySpinner;

    // ── State ─────────────────────────────────────────────────────────────────
    private Repository repository;
    private int excursionID = -1;   // -1 = new excursion
    private int vacationID = -1;
    private String vacStartDate = "";
    private String vacEndDate = "";
    private String pendingCategory = "";   // category to select once the spinner loads
    private static final String ADD_CATEGORY_OPTION = "Add new category...";
    private ArrayAdapter<String> categoryAdapter;

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ── Bind views — IDs match activity_excursion_details.xml ────────────
        editTitle = findViewById(R.id.excursionTitle);
        editDate = findViewById(R.id.excursionDate);
        categorySpinner = findViewById(R.id.excursionCategory);

        repository = new Repository(getApplication());

        // ── Date picker (B5c) — prevents bad date formats ────────────────────
        editDate.setFocusable(false);
        editDate.setLongClickable(false);
        editDate.setOnClickListener(v -> showDatePicker());

        // ── Read Intent extras ────────────────────────────────────────────────
        vacationID = getIntent().getIntExtra("vacID", -1);
        vacStartDate = getIntent().getStringExtra("vacStartDate") != null
                ? getIntent().getStringExtra("vacStartDate") : "";
        vacEndDate = getIntent().getStringExtra("vacEndDate") != null
                ? getIntent().getStringExtra("vacEndDate") : "";
        excursionID = getIntent().getIntExtra("id", -1);

        // ── Populate fields if editing an existing excursion (B5a) ───────────
        if (excursionID != -1) {
            getSupportActionBar().setTitle("Edit Excursion");
            editTitle.setText(getIntent().getStringExtra("title"));
            editDate.setText(getIntent().getStringExtra("date"));
            pendingCategory = getIntent().getStringExtra("category");
        } else {
            getSupportActionBar().setTitle("New Excursion");
        }

        setupCategorySpinner();
    }

    // ── Category spinner (scalable design — options come from the database) ───

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        refreshCategories(pendingCategory);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (categoryAdapter.getItem(position).equals(ADD_CATEGORY_OPTION)) {
                    promptForNewCategory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /** Reloads categories from the database and selects {@code categoryToSelect} if present. */
    private void refreshCategories(String categoryToSelect) {
        List<Category> categories = repository.getAllCategories();
        List<String> names = new ArrayList<>();
        for (Category c : categories) {
            names.add(c.getName());
        }
        names.add(ADD_CATEGORY_OPTION);

        categoryAdapter.clear();
        categoryAdapter.addAll(names);
        categoryAdapter.notifyDataSetChanged();

        if (categoryToSelect != null) {
            int index = names.indexOf(categoryToSelect);
            if (index >= 0) categorySpinner.setSelection(index);
        }
    }

    /** Shows a dialog to add a brand-new category — it's saved to the database
     * so it becomes available to every excursion from then on, not just this one. */
    private void promptForNewCategory() {
        EditText input = new EditText(this);
        input.setHint("New category name");

        new AlertDialog.Builder(this)
                .setTitle("Add Category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Category name cannot be empty.", Toast.LENGTH_SHORT).show();
                        refreshCategories(null);
                        return;
                    }
                    repository.insertCategory(new Category(0, name));
                    // Give the async insert a moment before re-querying (same pattern
                    // used elsewhere in this app for insert-then-read).
                    categorySpinner.postDelayed(() -> refreshCategories(name), 300);
                })
                .setNegativeButton("Cancel", (dialog, which) -> refreshCategories(null))
                .setCancelable(false)
                .show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Opens a DatePickerDialog and writes the chosen date into editDate.
     */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) ->
                        editDate.setText(String.format(Locale.US, "%02d/%02d/%04d",
                                month + 1, day, year)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * B5c – validates date format (picker guarantees format, but defensive).
     * B5e – validates excursion date falls within the parent vacation's dates.
     */
    private boolean validateInputs() {
        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();

        if (title.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // B5c
        if (!DateRangeValidator.isValidDate(dateStr)) {
            Toast.makeText(this,
                    "Date must be in MM/dd/yyyy format.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // B5e — excursion date must fall within vacation start and end dates
        if (!vacStartDate.isEmpty() && !vacEndDate.isEmpty()
                && !DateRangeValidator.isWithinRange(dateStr, vacStartDate, vacEndDate)) {
            Toast.makeText(this,
                    "Excursion date must be within the vacation:\n"
                            + vacStartDate + " – " + vacEndDate,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // ── Options menu ──────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.excursion_save) {
            saveExcursion();
            return true;
        }
        if (id == R.id.excursion_delete) {
            deleteExcursion();
            return true;
        }
        if (id == R.id.excursion_alert) {
            setAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ── Save / Update (B5b) ───────────────────────────────────────────────────

    private void saveExcursion() {
        if (!validateInputs()) return;

        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();
        String category = selectedCategory();

        if (excursionID == -1) {
            // New excursion — insert then retrieve auto-generated ID
            Excursion excursion = new Excursion(0, title, dateStr, vacationID, category);
            repository.insert(excursion);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            List<Excursion> all = repository.getAllExcursions();
            if (!all.isEmpty()) {
                excursionID = all.get(all.size() - 1).getExcursionID();
            }
            Toast.makeText(this, "Excursion saved!", Toast.LENGTH_SHORT).show();
        } else {
            // Existing excursion — update
            Excursion excursion = new Excursion(excursionID, title, dateStr, vacationID, category);
            repository.update(excursion);
            Toast.makeText(this, "Excursion updated!", Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setTitle("Edit Excursion");
    }

    /** Returns the spinner's current selection, or null if nothing valid is picked yet. */
    private String selectedCategory() {
        Object selected = categorySpinner.getSelectedItem();
        if (selected == null) return null;
        String value = selected.toString();
        return ADD_CATEGORY_OPTION.equals(value) ? null : value;
    }

    // ── Delete (B5b) ──────────────────────────────────────────────────────────

    private void deleteExcursion() {
        if (excursionID == -1) {
            Toast.makeText(this, "Nothing to delete yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();

        repository.delete(new Excursion(excursionID, title, dateStr, vacationID, selectedCategory()));
        Toast.makeText(this, "Excursion deleted.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ── Alert (B5d) ───────────────────────────────────────────────────────────

    private void setAlert() {
        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();

        if (title.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this,
                    "Fill in the excursion details first.", Toast.LENGTH_SHORT).show();
            return;
        }

        sdf.setLenient(false);
        Date alertDate;
        try {
            alertDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // B5d — message states the excursion title
        String message = title + " excursion is today!";

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", message);

        // Offset by 10000 so excursion codes never collide with vacation codes
        int requestCode = (excursionID != -1 ? excursionID : (int) System.currentTimeMillis())
                + 10000;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertDate.getTime(), pendingIntent);

        Toast.makeText(this,
                "Alert set for " + dateStr + ": \"" + message + "\"",
                Toast.LENGTH_LONG).show();
    }
}