package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class Login_user extends AppCompatActivity {

    private EditText editPseudo, editRoomCode;
    private Button btnJoin;
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user); // adapte à ton layout

        editPseudo = findViewById(R.id.editPseudo);       // champ pseudo
        editRoomCode = findViewById(R.id.editRoomCode);   // champ code de salle
        btnJoin = findViewById(R.id.btnJoin);             // bouton rejoindre

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // Connexion anonyme à Firebase
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Login", "Authentifié : " + mAuth.getCurrentUser().getUid());
                } else {
                    Log.e("Login", "Échec de connexion", task.getException());
                }
            });
        }

        btnJoin.setOnClickListener(v -> {
            String pseudo = editPseudo.getText().toString().trim();
            String roomCode = editRoomCode.getText().toString().trim();

            if (pseudo.isEmpty() || roomCode.isEmpty()) {
                Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            joinRoom(roomCode, pseudo);
        });
    }

    private void joinRoom(String roomCode, String pseudo) {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference roomRef = database.child("rooms").child(roomCode);

        // Vérifie que la salle existe
        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // La salle existe, ajoute l'utilisateur
                    DatabaseReference playerRef = roomRef.child("players").child(uid);

                    Map<String, Object> playerData = new HashMap<>();
                    playerData.put("name", pseudo);
                    playerData.put("score", 0);

                    playerRef.setValue(playerData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Rejoint la salle avec succès !", Toast.LENGTH_SHORT).show();
                        // TODO : aller vers l'écran de la salle
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur en rejoignant la salle", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Erreur ajout joueur", e);
                    });

                } else {
                    Toast.makeText(this, "Salle inexistante", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firebase", "Erreur lecture salle", task.getException());
            }
        });
    }
}
