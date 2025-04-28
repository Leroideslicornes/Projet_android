package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RejoindrePartie extends AppCompatActivity {

    private EditText editTextCode, editTextPseudo;
    private Button buttonJoin, buttonBack; // Ajout du bouton retour
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

        // Lier les vues
        editTextCode = findViewById(R.id.editTextCode);
        editTextPseudo = findViewById(R.id.editTextPseudo);
        buttonJoin = findViewById(R.id.buttonJoin);
        buttonBack = findViewById(R.id.buttonBack); // Lier le bouton retour

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        buttonJoin.setOnClickListener(view -> rejoindrePartie());

        // Gérer le clic sur le bouton retour
        buttonBack.setOnClickListener(view -> {
            Intent intent = new Intent(RejoindrePartie.this, MainActivity.class);
            startActivity(intent);
            finish(); // Facultatif : ferme l'activité actuelle pour éviter de revenir dessus
        });
    }

    private void rejoindrePartie() {
        String code = editTextCode.getText().toString().trim();
        String pseudo = editTextPseudo.getText().toString().trim();

        if (code.isEmpty() || pseudo.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference partieRef = db.collection("Partie").document("Partie_en_cours");

        partieRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean codeTrouve = false;
                String partieTrouvee = null;

                for (String key : documentSnapshot.getData().keySet()) {
                    if (key.startsWith("Partie_")) {
                        List<Object> partieList = (List<Object>) documentSnapshot.get(key);
                        if (partieList != null && partieList.size() > 1) {
                            Object codePartie = partieList.get(0);
                            Object etatPartie = partieList.get(1);

                            if (codePartie != null && etatPartie != null &&
                                    codePartie.toString().equals(code) &&
                                    etatPartie.toString().equalsIgnoreCase("en attente")) {
                                codeTrouve = true;
                                partieTrouvee = key;
                                break;
                            }
                        }
                    }
                }

                if (codeTrouve && partieTrouvee != null) {
                    Toast.makeText(this, "Code trouvé et partie en attente !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, WaitingRoomJoueur.class);
                    intent.putExtra("PSEUDO", pseudo);
                    intent.putExtra("NUM_SALLE", partieTrouvee);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "La partie n'est pas en attente ou code incorrect.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Le document Partie_en_cours n'existe pas.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur Firebase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
