package my.edu.utar.assignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btnAdd, btnClearAll;

    ArrayList<PasswordModel> list;
    ArrayAdapter<String> adapter;

    StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI
        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        btnClearAll = findViewById(R.id.btnClearAll);

        // Initialize storage helper
        storageHelper = new StorageHelper(this);

        // Add new entry
        btnAdd.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddEditActivity.class));
        });

        // Clear all data button
        btnClearAll.setOnClickListener(view -> {
            showClearAllConfirmation();
        });

        // Click item to edit
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("index", position);
            startActivity(intent);
        });

        // Long press to delete with confirmation
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    // Show delete confirmation dialog
    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this password entry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list = storageHelper.getAll();
                        if (position >= 0 && position < list.size()) {
                            storageHelper.deleteEntry(position);
                            refreshList();
                            Toast.makeText(MainActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearAllConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to delete ALL password entries? This action cannot be undone.")
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storageHelper.clearAllData();
                        refreshList();
                        Toast.makeText(MainActivity.this, "All data cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list every time the activity resumes
        refreshList();
    }

    private void refreshList() {
        // Reload saved data
        list = storageHelper.getAll();

        // Convert objects to string for display (hide actual passwords)
        ArrayList<String> displayList = new ArrayList<>();
        for (PasswordModel p : list) {
            displayList.add(p.getSite() + " (" + p.getUsername() + ")");
        }

        // Update adapter
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(displayList);
            adapter.notifyDataSetChanged();
        }
    }
}