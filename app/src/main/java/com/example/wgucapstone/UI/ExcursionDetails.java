package com.example.wgucapstone.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgucapstone.R;
import com.example.wgucapstone.database.Repository;
import com.example.wgucapstone.entities.Excursion;
import com.example.wgucapstone.receivers.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────────────────
    private EditText editTitle;
    private EditText editDate;

    // ── State ─────────────────────────────────────────────────────────────────
    private Repository repository;
    private int excursionID = -1;   // -1 = new excursion
    private int vacationID = -1;
    private String vacStartDate = "";
    private String vacEndDate = "";

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
        } else {
            getSupportActionBar().setTitle("New Excursion");
        }
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

        sdf.setLenient(false);
        Date excDate;
        try {
            excDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            // B5c
            Toast.makeText(this,
                    "Date must be in MM/dd/yyyy format.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // B5e — excursion date must fall within vacation start and end dates
        if (!vacStartDate.isEmpty() && !vacEndDate.isEmpty()) {
            try {
                Date startDate = sdf.parse(vacStartDate);
                Date endDate = sdf.parse(vacEndDate);
                if (excDate.before(startDate) || excDate.after(endDate)) {
                    Toast.makeText(this,
                            "Excursion date must be within the vacation:\n"
                                    + vacStartDate + " – " + vacEndDate,
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException ignored) {
                // vacation dates unavailable — skip range check
            }
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

        if (excursionID == -1) {
            // New excursion — insert then retrieve auto-generated ID
            Excursion excursion = new Excursion(0, title, dateStr, vacationID);
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
            Excursion excursion = new Excursion(excursionID, title, dateStr, vacationID);
            repository.update(excursion);
            Toast.makeText(this, "Excursion updated!", Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setTitle("Edit Excursion");
    }

    // ── Delete (B5b) ──────────────────────────────────────────────────────────

    private void deleteExcursion() {
        if (excursionID == -1) {
            Toast.makeText(this, "Nothing to delete yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();

        repository.delete(new Excursion(excursionID, title, dateStr, vacationID));
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