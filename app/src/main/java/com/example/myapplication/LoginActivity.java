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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference partiesRef = db.collection("Partie").document("Partie_en_cours");

        partiesRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int partieIndex = 1;
                        String partieName;
                        List<Object> partieList;

                        // Cherche la première Partie_X qui n'existe pas
                        while (true) {
                            partieName = "Partie_" + partieIndex;
                            partieList = (List<Object>) documentSnapshot.get(partieName);
                            if (partieList == null) {
                                break; // Partie libre trouvée
                            }
                            partieIndex++;
                        }

                        // Créer la nouvelle partie
                        List<Object> nouvellePartie = new ArrayList<>();
                        nouvellePartie.add(roomCode); // Position 0 : code de la salle
                        nouvellePartie.add(status);   // Position 1 : statut de la salle

                        // Mettre à jour dans Firestore
                        int finalPartieIndex = partieIndex;
                        String finalPartieName = partieName;
                        partiesRef.update(partieName, nouvellePartie)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Salle créée : " + roomCode, Toast.LENGTH_SHORT).show();
                                    createPlayerDocument(finalPartieName);
                                    goToMainWaitingRoom("Partie_" + finalPartieIndex);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erreur lors de la création", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Erreur création salle", e);
                                });
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

    private void goToMainWaitingRoom(String NumSalle) {
        Intent intent = new Intent(LoginActivity.this, com.example.myapplication.WaitingRoom.class);
        intent.putExtra("NumSalle", NumSalle);
        startActivity(intent);
        finish();
    }

    private void createPlayerDocument(String numaPortie) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Nom du document : numaportie + "_players"
        String documentName = numaPortie + "_players";

        // Création de la structure du document avec une liste vide pour les joueurs
        Map<String, Object> partieData = new HashMap<>();

        // Référence vers le document
        DocumentReference documentRef = db.collection("Partie").document(documentName);

        // Créer le document
        documentRef.set(partieData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Document créé avec succès : " + documentName, Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Document créé : " + documentName);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de la création du document", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Erreur lors de la création du document", e);
                });
    }

}
