package com.example.d308vacationplanner.UI;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.database.Repository;
import com.example.d308vacationplanner.entities.Excursion;
import com.example.d308vacationplanner.entities.Vacation;
import com.example.d308vacationplanner.receivers.AlarmReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    // ── UI ────────────────────────────────────────────────────────────────────
    private EditText editTitle;
    private EditText editHotel;
    private EditText editStartDate;
    private EditText editEndDate;

    // ── State ─────────────────────────────────────────────────────────────────
    private Repository repository;
    private int vacationID = -1;          // -1 = new vacation
    private ExcursionAdapter excursionAdapter;

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ── Bind views — IDs match activity_vacation_details.xml ─────────────
        editTitle     = findViewById(R.id.vacationTitle);
        editHotel     = findViewById(R.id.vacationHotel);
        editStartDate = findViewById(R.id.vacationStartDate);
        editEndDate   = findViewById(R.id.vacationEndDate);

        repository = new Repository(getApplication());

        // ── Date pickers (B3c) — prevents bad date formats ───────────────────
        editStartDate.setFocusable(false);
        editStartDate.setLongClickable(false);
        editEndDate.setFocusable(false);
        editEndDate.setLongClickable(false);
        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate));

        // ── Populate fields if editing an existing vacation (B3a) ─────────────
        vacationID = getIntent().getIntExtra("id", -1);
        if (vacationID != -1) {
            getSupportActionBar().setTitle("Edit Vacation");
            editTitle.setText(getIntent().getStringExtra("title"));
            editHotel.setText(getIntent().getStringExtra("hotel"));
            editStartDate.setText(getIntent().getStringExtra("startDate"));
            editEndDate.setText(getIntent().getStringExtra("endDate"));
        } else {
            getSupportActionBar().setTitle("New Vacation");
        }

        // ── FAB → navigate to ExcursionDetails to add a new excursion (B3h) ──
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(v -> {
            if (vacationID == -1) {
                Toast.makeText(this,
                        "Please save the vacation first before adding excursions.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
            intent.putExtra("vacID",        vacationID);
            intent.putExtra("vacStartDate", editStartDate.getText().toString().trim());
            intent.putExtra("vacEndDate",   editEndDate.getText().toString().trim());
            startActivity(intent);
        });

        // ── Excursion RecyclerView (B3g) ──────────────────────────────────────
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshExcursionList();
    }

    // Refresh the excursion list every time we return to this screen (B3g)
    @Override
    protected void onResume() {
        super.onResume();
        refreshExcursionList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void refreshExcursionList() {
        if (vacationID != -1) {
            excursionAdapter.setVacationDates(
                    editStartDate.getText().toString().trim(),
                    editEndDate.getText().toString().trim());
            excursionAdapter.setExcursions(
                    repository.getExcursionsByVacation(vacationID));
        }
    }

    /** Opens a DatePickerDialog and writes the chosen date into the target EditText. */
    private void showDatePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) ->
                        target.setText(String.format(Locale.US, "%02d/%02d/%04d",
                                month + 1, day, year)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * B3c – date format validation (guaranteed by picker, but defensive).
     * B3d – end date must be strictly after start date.
     */
    private boolean validateInputs() {
        String title    = editTitle.getText().toString().trim();
        String hotel    = editHotel.getText().toString().trim();
        String startStr = editStartDate.getText().toString().trim();
        String endStr   = editEndDate.getText().toString().trim();

        if (title.isEmpty() || hotel.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        sdf.setLenient(false);
        Date startDate, endDate;
        try {
            startDate = sdf.parse(startStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Start date must be MM/dd/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            endDate = sdf.parse(endStr);
        } catch (ParseException e) {
            Toast.makeText(this, "End date must be MM/dd/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // B3d
        if (!endDate.after(startDate)) {
            Toast.makeText(this, "End date must be after start date.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // ── Options menu ──────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)         { finish();          return true; }
        if (id == R.id.vacation_save)        { saveVacation();    return true; }
        if (id == R.id.vacation_delete)      { deleteVacation();  return true; }
        if (id == R.id.vacation_share)       { shareVacation();   return true; }
        if (id == R.id.vacation_alert_start) { setAlert(true);    return true; }
        if (id == R.id.vacation_alert_end)   { setAlert(false);   return true; }
        return super.onOptionsItemSelected(item);
    }

    // ── Save / Update (B3b) ───────────────────────────────────────────────────

    private void saveVacation() {
        if (!validateInputs()) return;

        String title     = editTitle.getText().toString().trim();
        String hotel     = editHotel.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate   = editEndDate.getText().toString().trim();

        if (vacationID == -1) {
            // New vacation — insert and retrieve auto-generated ID
            Vacation vacation = new Vacation(0, title, hotel, startDate, endDate);
            repository.insert(vacation);

            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            List<Vacation> all = repository.getmAllVacations();
            if (!all.isEmpty()) {
                vacationID = all.get(all.size() - 1).getVacationID();
            }
            Toast.makeText(this, "Vacation saved!", Toast.LENGTH_SHORT).show();
        } else {
            // Existing vacation — update
            Vacation vacation = new Vacation(vacationID, title, hotel, startDate, endDate);
            repository.update(vacation);
            Toast.makeText(this, "Vacation updated!", Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setTitle("Edit Vacation");
    }

    // ── Delete (B1b + B3b) ────────────────────────────────────────────────────

    private void deleteVacation() {
        if (vacationID == -1) {
            Toast.makeText(this, "Nothing to delete yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // B1b — block delete if excursions exist
        List<Excursion> excursions = repository.getExcursionsByVacation(vacationID);
        if (excursions != null && !excursions.isEmpty()) {
            Toast.makeText(this,
                    "Cannot delete: vacation has " + excursions.size()
                            + " excursion(s). Delete them first.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String title     = editTitle.getText().toString().trim();
        String hotel     = editHotel.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate   = editEndDate.getText().toString().trim();

        repository.delete(new Vacation(vacationID, title, hotel, startDate, endDate));
        Toast.makeText(this, "Vacation deleted.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ── Share (B3f) ───────────────────────────────────────────────────────────

    private void shareVacation() {
        String title     = editTitle.getText().toString().trim();
        String hotel     = editHotel.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate   = editEndDate.getText().toString().trim();

        StringBuilder sb = new StringBuilder()
                .append("Vacation: ").append(title)
                .append("\nHotel: ").append(hotel)
                .append("\nStart: ").append(startDate)
                .append("\nEnd: ").append(endDate);

        if (vacationID != -1) {
            List<Excursion> excursions = repository.getExcursionsByVacation(vacationID);
            if (excursions != null && !excursions.isEmpty()) {
                sb.append("\n\nExcursions:");
                for (Excursion e : excursions) {
                    sb.append("\n  - ").append(e.getTitle())
                            .append(" on ").append(e.getDate());
                }
            }
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation: " + title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Share vacation via..."));
    }

    // ── Alerts (B3e) ──────────────────────────────────────────────────────────

    private void setAlert(boolean isStart) {
        String title   = editTitle.getText().toString().trim();
        String dateStr = isStart
                ? editStartDate.getText().toString().trim()
                : editEndDate.getText().toString().trim();

        if (title.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this,
                    "Fill in the vacation details first.", Toast.LENGTH_SHORT).show();
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

        String message = title + (isStart ? " is starting today!" : " is ending today!");

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", message);

        int requestCode = (vacationID != -1 ? vacationID : 9999) * 10 + (isStart ? 0 : 1);

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