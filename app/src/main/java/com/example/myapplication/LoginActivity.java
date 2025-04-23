package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Realtime Database
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Firestore (pour la vérification login)
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference database;
    private EditText emailEditText, passwordEditText;
    private Button createButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase init
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // UI
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        createButton = findViewById(R.id.btnCreateGame);
        backButton = findViewById(R.id.btnBackToMain);

        // Écouteurs boutons
        createButton.setOnClickListener(view -> registerUser());
        backButton.setOnClickListener(view -> goToMainActivity());
    }

    private void registerUser() {
        String user = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Champs vides", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Users")
                .document("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String dbUser = document.getString("Num_utilisateur");
                            String dbPassword = document.getString("Mdp_utilisateur");

                            if (user.equals(dbUser) && password.equals(dbPassword)) {
                                updatePartie(); // passe le pseudo
                            } else {
                                Toast.makeText(this, "Utilisateur non reconnu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Erreur lors de l'accès à Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePartie() {
        String roomCode = generateRoomCode(); // Génère un code unique pour la salle
        String status = "en attente"; // Le statut de la salle

        // Référence à Firestore pour la collection Partie et le document Partie_en_cours
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference partiesRef = db.collection("Partie").document("Partie_en_cours");

        partiesRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer le tableau "Partie_1" du document
                        List<Object> partieList = (List<Object>) documentSnapshot.get("Partie_1");

                        if (partieList != null) {
                            // Met à jour ou ajoute les valeurs
                            if (partieList.size() > 0) {
                                partieList.set(0, roomCode);
                            } else {
                                partieList.add(0, roomCode);
                            }

                            if (partieList.size() > 1) {
                                partieList.set(1, status);
                            } else {
                                partieList.add(1, status);
                            }

                            // Mettre à jour dans Firestore
                            partiesRef.update("Partie_1", partieList)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Salle mise à jour : " + roomCode, Toast.LENGTH_SHORT).show();
                                        partiesRef.update("Partie1_players", new ArrayList<String>());
                                        goToMainWaitingRoom();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                                        Log.e("Firebase", "Erreur mise à jour salle", e);
                                    });
                        } else {
                            Toast.makeText(this, "Le tableau Partie_1 est vide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Le document Partie_en_cours n'existe pas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de récupération du document", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Erreur de récupération du document", e);
                });
    }



    private String generateRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    private void goToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void goToMainWaitingRoom() {
        Intent intent = new Intent(LoginActivity.this, com.example.myapplication.WaitingRoom.class);
        startActivity(intent);
        finish();
    }
}
