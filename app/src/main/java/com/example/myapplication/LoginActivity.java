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

        // Crée un objet avec les données à mettre à jour
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("roomCode", roomCode); // Le code de la salle

        Map<String, Object> statusData = new HashMap<>();
        statusData.put("status", status); // Le statut de la salle

        // Récupère le tableau actuel "Partie_1" et le met à jour à l'index spécifique (index 0 pour roomCode et index 1 pour status)
        partiesRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer le tableau "Partie_1" du document
                        List<Map<String, Object>> partieList = (List<Map<String, Object>>) documentSnapshot.get("Partie_1");

                        // Assurez-vous que le tableau a au moins deux éléments pour éviter des erreurs
                        if (partieList != null) {
                            // Modifie l'élément à l'index 0 (roomCode) et l'index 1 (status)
                            if (partieList.size() > 0) {
                                partieList.set(0, roomData);  // Met à jour le code de la salle à l'index 0
                            } else {
                                // Si l'index 0 n'existe pas, ajoute-le
                                partieList.add(0, roomData);
                            }
                            if (partieList.size() > 1) {
                                partieList.set(1, statusData);  // Met à jour le statut à l'index 1
                            } else {
                                // Si l'index 1 n'existe pas, ajoute-le
                                partieList.add(1, statusData);
                            }

                            // Met à jour le tableau dans Firestore
                            partiesRef.update("Partie_1", partieList)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Salle mise à jour : " + roomCode, Toast.LENGTH_SHORT).show();
                                        goToMainWaitingRoom(); // Passe le code de la salle
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                                        Log.e("Firebase", "Erreur mise à jour salle", e);
                                    });
                        } else {
                            Toast.makeText(this, "Le tableau Partie_1 est vide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Le document "Partie_en_cours" n'existe pas
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
