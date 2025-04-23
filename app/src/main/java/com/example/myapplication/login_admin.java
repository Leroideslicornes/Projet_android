package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class login_admin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Déclare Firestore

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;
    private TextView questionTextView; // Déclare un TextView pour afficher la question
    private RadioGroup choiceRadioGroup; // Déclare un RadioGroup pour les choix
    private String correctAnswer; // Stocke la bonne réponse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        // Initialiser Firebase
        FirebaseApp.initializeApp(this); // Initialiser Firebase

        // Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialisation de Firestore

        // Liaison avec le layout
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);


        // Vérifie si l'utilisateur est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }

        // Bouton Connexion
        loginButton.setOnClickListener(view -> loginUser());

        // Bouton Inscription
        signupButton.setOnClickListener(view -> registerUser());

        // Récupérer et afficher la question1 depuis Firestore
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login_admin.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(login_admin.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        User newUser = new User(email);

                        Toast.makeText(login_admin.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(login_admin.this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(login_admin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    public static class User {
        public String email;

        public User() {}

        public User(String email) {
            this.email = email;
        }
    }
}