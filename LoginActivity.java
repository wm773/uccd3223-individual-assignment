package my.edu.utar.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Declare UI components
    EditText etPassword;
    Button btnLogin;

    // Hardcoded password for authentication
    private final String MASTER_PASSWORD = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link UI elements with XML
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get input password
                String input = etPassword.getText().toString();

                // Check if password is correct
                if (input.equals(MASTER_PASSWORD)) {

                    // Navigate to MainActivity if correct
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // Close login screen

                } else {
                    // Show error message
                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
