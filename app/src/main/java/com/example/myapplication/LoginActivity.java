package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_login);

        // Initialiser Firebase
        FirebaseApp.initializeApp(this); // Initialiser Firebase

        // Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialisation de Firestore

        // Liaison avec le layout
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        questionTextView = findViewById(R.id.questionTextView); // Initialiser le TextView
        choiceRadioGroup = findViewById(R.id.choiceRadioGroup); // Initialiser le RadioGroup

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
        getQuestionFromFirestore();
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
                        Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(LoginActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getQuestionFromFirestore() {
        // Récupère le document "7WyY0brE6bBKfvDThSuf1" de la collection "Quiz"
        db.collection("Quiz")
                .document("7WyY0brE6BKfvDThSuf1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Récupère la question1 et les réponses
                            String question1 = document.getString("question1");
                            List<String> choices = (List<String>) document.get("choices"); // Tableau avec les choix et la bonne réponse

                            if (question1 != null && choices != null && choices.size() == 5) {
                                // Affiche la question
                                questionTextView.setText(question1);

                                // Mettre les 4 choix dans les RadioButton
                                ((RadioButton) findViewById(R.id.choiceRadioButton1)).setText(choices.get(0));
                                ((RadioButton) findViewById(R.id.choiceRadioButton2)).setText(choices.get(1));
                                ((RadioButton) findViewById(R.id.choiceRadioButton3)).setText(choices.get(2));
                                ((RadioButton) findViewById(R.id.choiceRadioButton4)).setText(choices.get(3));

                                // La réponse correcte est stockée dans choices.get(4)
                                correctAnswer = choices.get(4);

                                // Vérifier la réponse sélectionnée
                                choiceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                                    RadioButton selectedRadioButton = findViewById(checkedId);
                                    if (selectedRadioButton != null && selectedRadioButton.getText().toString().equals(correctAnswer)) {
                                        Toast.makeText(LoginActivity.this, "Bonne réponse !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Mauvaise réponse. Essayez encore.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                questionTextView.setText("Erreur : Données incorrectes.");
                            }
                        } else {
                            questionTextView.setText("Document non trouvé.");
                        }
                    } else {
                        Log.w("Firestore", "Erreur lors de la récupération du document.", task.getException());
                        questionTextView.setText("Erreur lors de la récupération de la question.");
                    }
                });
    }

    public static class User {
        public String email;

        public User() {}

        public User(String email) {
            this.email = email;
        }
    }
}
