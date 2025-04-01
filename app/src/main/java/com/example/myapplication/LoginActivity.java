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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        loginButton.setOnClickListener(view -> registerUser());

        // Bouton Inscription
        signupButton.setOnClickListener(view -> registerUser());

        // Récupérer et afficher la question1 depuis Firestore
        getQuestionFromFirestore();
    }
    private String generateRandomCode() {
        Random random = new Random();
        int code = random.nextInt(1000000);  // Code entre 0 et 999999
        return String.format("%06d", code);  // Formater pour avoir 6 chiffres, par exemple
    }
    private void saveCodeAndStateToFirestore(String randomCode) {
        // Créer un tableau avec le code aléatoire à la position [0] et l'état "en attente" à la position [1]
        List<String> partieData = Arrays.asList(randomCode, "en attente");

        // Mettre à jour le champ "Partie_1" dans le document "Partie_en_cours" de la collection "Partie"
        db.collection("Partie")
                .document("Partie_en_cours")
                .update("Partie_1", partieData)  // Mise à jour du champ "Partie_1" avec le tableau
                .addOnSuccessListener(aVoid -> {
                    // Succès de l'écriture
                    Toast.makeText(LoginActivity.this, "Code et état enregistrés avec succès", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Erreur lors de l'écriture
                    Toast.makeText(LoginActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }


    private void registerUser() {
        String user = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        db.collection("Users")
                .document("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String Num_utilisateur = document.getString("Num_utilisateur");
                            String Mdp_utilisateur = document.getString("Mdp_utilisateur");

                            if (user.equals(Num_utilisateur) && password.equals(Mdp_utilisateur)) {

                                String randomCode = generateRandomCode();
                                saveCodeAndStateToFirestore(randomCode);

                            } else {
                                Toast.makeText(this, "Utilisateur non reconue", Toast.LENGTH_SHORT).show();
                            }
                        }
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
