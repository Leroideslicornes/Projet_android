package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String Numsalle = getIntent().getStringExtra("NUM_SALLE");

        ajouterPseudoDansPartie(pseudo,Numsalle);

// Écoute les changements en temps réel
        partieRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Erreur
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<Object> partie1 = (List<Object>) documentSnapshot.get(Numsalle);
                if (partie1 != null && partie1.size() > 1) {
                    String etat = partie1.get(1).toString();
                    if (etat.equals("Partie en cours")) {
                        // La partie a commencé !
                        Toast.makeText(this, "La partie a commencé !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WaitingRoomJoueur.this, Quizz.class);
                        intent.putExtra("NunSalle", Numsalle);
                        startActivity(intent);

                    }
                }
            }
        });

    }

    private void ajouterPseudoDansPartie(String pseudo, String Numsalle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (pseudo != null) {
            textViewPseudo.setText("Bienvenue " + pseudo + " ! dans la " + Numsalle);
        } else {
            textViewPseudo.setText("Bienvenue !");
        }

        // Référence au document de la salle avec le nom numsalle + "_players"
        DocumentReference partieRef = db.collection("Partie").document(Numsalle + "_players");

        // Créer une Map pour associer le pseudo au score
        Map<String, Object> playerScore = new HashMap<>();
        playerScore.put(pseudo, 0); // Le pseudo comme clé, et le score comme valeur (0 au début)

        // Mise à jour du document en ajoutant le pseudo et le score
        partieRef.update(playerScore)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pseudo et score ajoutés à la salle !", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur d'ajout : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
