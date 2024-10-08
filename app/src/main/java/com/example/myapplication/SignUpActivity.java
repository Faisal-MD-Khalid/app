package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword;
    Spinner religionSpinner, classificationSpinner;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        religionSpinner = findViewById(R.id.signup_religion_spinner);
        classificationSpinner = findViewById(R.id.classification_spinner);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.religions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        religionSpinner.setAdapter(adapter);
        religionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateClassificationSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Add a text change listener to the username field
        signupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkUsernameExistence(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void signUp() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        final String name = signupName.getText().toString();
        final String email = signupEmail.getText().toString();
        final String username = signupUsername.getText().toString();
        final String password = signupPassword.getText().toString();
        final String signup_religion_spinner = religionSpinner.getSelectedItem().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, set display name
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Get the unique UID
                            String uid = user.getUid();

                            // Set the display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username) // Set the username as display name
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Send verification email after setting display name
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            // Email verification sent
                                                            Helper helperClass = new Helper(name, email, username, password, signup_religion_spinner);

                                                            // Store data using UID as key
                                                            reference.child(uid).setValue(helperClass);

                                                            Toast.makeText(SignUpActivity.this, "Please verify your email address!", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            // Failed to send verification email
                                                            Toast.makeText(SignUpActivity.this, "Failed to send verification email!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // Failed to update display name
                                            Toast.makeText(SignUpActivity.this, "Failed to set display name.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Sign up failed
                        Toast.makeText(SignUpActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUsernameExistence(final String username) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username exists, show message
                    signupUsername.setError("Username already exists!");
                    signupButton.setEnabled(false); // Disable signup button
                } else {
                    // Username doesn't exist, clear error and enable signup button
                    signupUsername.setError(null);
                    signupButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SignupActivity", "Error checking username existence", databaseError.toException());
            }
        });
    }

    private void populateClassificationSpinner(int position) {
        ArrayAdapter<CharSequence> classificationAdapter;
        switch (position) {
            case 0: // Muslim
                classificationAdapter = ArrayAdapter.createFromResource(this,
                        R.array.muslim_classifications, android.R.layout.simple_spinner_item);
                break;
            case 1: // Hindu
                classificationAdapter = ArrayAdapter.createFromResource(this,
                        R.array.hindu_classifications, android.R.layout.simple_spinner_item);
                break;
            case 2: // Christian
                classificationAdapter = ArrayAdapter.createFromResource(this,
                        R.array.christian_classifications, android.R.layout.simple_spinner_item);
                break;
            default:
                classificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{});
                break;
        }
        classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classificationSpinner.setAdapter(classificationAdapter);
    }
}
















