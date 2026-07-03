package com.example.wgucapstone.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgucapstone.R;
import com.example.wgucapstone.security.PinHasher;

/**
 * App-entry security gate. On first launch (no PIN saved) it lets the user
 * create one; on every launch after that it requires the correct PIN before
 * MainActivity is reachable. The PIN itself is never stored — only a random
 * salt and the salted SHA-256 hash are kept in SharedPreferences.
 */
public class PinLockActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "pin_lock";
    private static final String KEY_SALT = "salt";
    private static final String KEY_HASH = "hash";
    private static final int MIN_PIN_LENGTH = 4;

    private SharedPreferences prefs;
    private boolean setupMode;

    private TextView title;
    private TextView subtitle;
    private TextView errorView;
    private EditText pinInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setupMode = prefs.getString(KEY_HASH, null) == null;

        title        = findViewById(R.id.pinTitle);
        subtitle     = findViewById(R.id.pinSubtitle);
        errorView    = findViewById(R.id.pinError);
        pinInput     = findViewById(R.id.pinInput);
        submitButton = findViewById(R.id.pinSubmit);

        if (setupMode) {
            title.setText("Create PIN");
            subtitle.setText("Choose a PIN (at least " + MIN_PIN_LENGTH
                    + " digits) to protect your vacations.");
            submitButton.setText("Set PIN");
        } else {
            title.setText("Enter PIN");
            subtitle.setText("Enter your PIN to unlock Vacation Planner.");
            submitButton.setText("Unlock");
        }

        submitButton.setOnClickListener(v -> {
            if (setupMode) handleCreatePin();
            else handleUnlock();
        });
    }

    private void handleCreatePin() {
        String pin = pinInput.getText().toString().trim();
        if (pin.length() < MIN_PIN_LENGTH) {
            showError("PIN must be at least " + MIN_PIN_LENGTH + " digits.");
            return;
        }

        String salt = PinHasher.generateSalt();
        String hash = PinHasher.hash(pin, salt);

        prefs.edit()
                .putString(KEY_SALT, salt)
                .putString(KEY_HASH, hash)
                .apply();

        proceedToApp();
    }

    private void handleUnlock() {
        String pin = pinInput.getText().toString().trim();
        String salt = prefs.getString(KEY_SALT, "");
        String storedHash = prefs.getString(KEY_HASH, "");

        if (PinHasher.hash(pin, salt).equals(storedHash)) {
            proceedToApp();
        } else {
            showError("Incorrect PIN. Please try again.");
            pinInput.setText("");
        }
    }

    private void showError(String message) {
        errorView.setText(message);
        errorView.setVisibility(TextView.VISIBLE);
    }

    private void proceedToApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
