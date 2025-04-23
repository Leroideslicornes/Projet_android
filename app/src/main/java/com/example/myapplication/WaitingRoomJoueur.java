package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WaitingRoomJoueur extends AppCompatActivity {

    private TextView textViewPseudo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_joueur);

        // Lier la vue
        textViewPseudo = findViewById(R.id.textViewPseudo);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference partieRef = db.collection("Partie").document("Partie_en_cours");
        String pseudo = getIntent().getStringExtra("PSEUDO");

        ajouterPseudoDansPartie(pseudo);

// Écoute les changements en temps réel
        partieRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Erreur
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<Object> partie1 = (List<Object>) documentSnapshot.get("Partie_1");
                if (partie1 != null && partie1.size() > 1) {
                    String etat = partie1.get(1).toString();
                    if (etat.equals("Partie en cours")) {
                        // La partie a commencé !
                        Toast.makeText(this, "La partie a commencé !", Toast.LENGTH_SHORT).show();

                        // Lance la prochaine activité par exemple :
                        // Intent intent = new Intent(this, Quizz.class);
                        // startActivity(intent);
                    }
                }
            }
        });

    }
    private void ajouterPseudoDansPartie(String pseudo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference partieRef = db.collection("Partie").document("Partie_en_cours");

        if (pseudo != null) {
            textViewPseudo.setText("Bienvenue " + pseudo + " !");
        } else {
            textViewPseudo.setText("Bienvenue !");
        }

        partieRef.update("Partie1_players", FieldValue.arrayUnion(pseudo))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pseudo ajouté à la partie !", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur d'ajout : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
