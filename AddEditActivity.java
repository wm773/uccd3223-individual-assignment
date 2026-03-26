package my.edu.utar.assignment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddEditActivity extends AppCompatActivity {

    EditText etSite, etUser, etPass;
    Button btnSave, btnDelete;

    StorageHelper storageHelper;
    ArrayList<PasswordModel> list;

    int index = -1; // -1 means new entry

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Initialize UI
        etSite = findViewById(R.id.etSite);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        storageHelper = new StorageHelper(this);
        list = storageHelper.getAll();

        // Check if editing existing item
        if (getIntent().hasExtra("index")) {
            index = getIntent().getIntExtra("index", -1);

            // Check if index is valid
            if (index >= 0 && index < list.size()) {
                PasswordModel p = list.get(index);
                etSite.setText(p.getSite());
                etUser.setText(p.getUsername());
                etPass.setText(p.getPassword());
                btnDelete.setVisibility(Button.VISIBLE); // Show delete button for existing entries
            }
        } else {
            btnDelete.setVisibility(Button.GONE); // Hide delete button for new entries
        }

        btnSave.setOnClickListener(view -> {
            // Get user input
            String site = etSite.getText().toString().trim();
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            // Validate input
            if (site.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            PasswordModel passwordModel = new PasswordModel(site, user, pass);

            if (index == -1) {
                // Add new entry
                storageHelper.addEntry(passwordModel);
            } else {
                // Update existing entry
                storageHelper.updateEntry(index, passwordModel);
            }

            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // go back
        });

        btnDelete.setOnClickListener(view -> {
            showDeleteConfirmation();
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this password entry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storageHelper.deleteEntry(index);
                        Toast.makeText(AddEditActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}