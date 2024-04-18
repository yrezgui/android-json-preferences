package com.example.jsonpreferences;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    static String filename = "preferences.json";
    String[] TARGET_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TARGET_FILE = getResources().getStringArray(R.array.preferences_filename_values);

        findViewById(R.id.resetAction).setOnClickListener(v -> reset());

        findViewById(R.id.readPreferencesFileAction).setOnClickListener(v -> {
            try {
                readPreferences();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        findViewById(R.id.writePreferencesFileAction).setOnClickListener(v -> {
            try {
                writePreferences();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        AutoCompleteTextView targetFolder = (AutoCompleteTextView) findViewById(R.id.targetFolderInput);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TARGET_FILE);
        targetFolder.setAdapter(adapter);
    }

    private void reset() {
        ((CheckBox) findViewById(R.id.isDarkModeEnabledCheckbox)).setChecked(false);
        ((EditText) findViewById(R.id.usernameInput)).setText(getText(R.string.username_input_default_value));
        ((EditText) findViewById(R.id.currentLevelInput)).setText(getText(R.string.current_level_default_value));
        ((AutoCompleteTextView) findViewById(R.id.targetFolderInput)).setText(TARGET_FILE[0], false);
    }

    private File getPreferencesFile(String target) {
        if (TARGET_FILE[0].equals(target)) {
            return new File(getFilesDir(), filename);
        } else if (TARGET_FILE[1].equals(target)) {
            return new File(getExternalFilesDir(null), filename);
        } else if (TARGET_FILE[2].equals(target)) {
            return new File(getCacheDir(), filename);
        }

        throw new IllegalArgumentException("Invalid target: " + target);
    }

    private void readPreferences() throws FileNotFoundException {
        AutoCompleteTextView targetFileInput = findViewById(R.id.targetFolderInput);
        String preferencesTarget = targetFileInput.getText().toString();
        File preferencesFile = getPreferencesFile(preferencesTarget);

        Gson gson = new Gson();
        String preferencesJson = new Scanner(preferencesFile).useDelimiter("\\Z").next();
        AppPreferences preferences = gson.fromJson(preferencesJson, AppPreferences.class);
        Log.d("readPreferences", preferencesFile.getAbsolutePath() + " read");

        CheckBox isDarkModeEnabledCheckbox = findViewById(R.id.isDarkModeEnabledCheckbox);
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText currentLevelInput = findViewById(R.id.currentLevelInput);

        isDarkModeEnabledCheckbox.setChecked(preferences.isDarkModeEnabled());
        usernameInput.setText(preferences.getUsername());
        currentLevelInput.setText(String.valueOf(preferences.getCurrentLevel()));
    }

    private void writePreferences() throws IOException {
        CheckBox isDarkModeEnabledCheckbox = findViewById(R.id.isDarkModeEnabledCheckbox);
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText currentLevelInput = findViewById(R.id.currentLevelInput);
        AutoCompleteTextView targetFileInput = findViewById(R.id.targetFolderInput);

        boolean isDarkModeEnabled = isDarkModeEnabledCheckbox.isChecked();
        String username = usernameInput.getText().toString();
        int currentLevel = Integer.parseInt(currentLevelInput.getText().toString());
        String preferencesTarget = targetFileInput.getText().toString();

        File preferencesFile = getPreferencesFile(preferencesTarget);

        if (!preferencesFile.exists()) {
            preferencesFile.createNewFile();
        }

        AppPreferences preferences = new AppPreferences(isDarkModeEnabled, username, currentLevel);
        Gson gson = new Gson();
        String json = gson.toJson(preferences);

        FileOutputStream preferencesOutputStream = new FileOutputStream(preferencesFile);
        preferencesOutputStream.write(json.getBytes());
        preferencesOutputStream.close();

        Log.d("writePreferences", preferencesFile.getAbsolutePath() + " saved");
        Toast.makeText(this, preferencesFile.getAbsolutePath() + " saved", Toast.LENGTH_LONG).show();
    }
}