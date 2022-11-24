package com.ilhamirfan.modulstorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.ilhamirfan.modulstorage.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailInput, passwordInput;
    private Button loginButton;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.EmailInput);
        passwordInput = findViewById(R.id.PasswordInput);
        loginButton = findViewById(R.id.loginBtn);

        storage = FirebaseStorage.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnRequestLogin(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });
    }

    private void OnRequestLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent noteActivity = new Intent(MainActivity.this, StorageActivity.class);
                            startActivity(noteActivity);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent homeActivity = new Intent(MainActivity.this, StorageActivity.class);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeActivity);
        }
    }
}