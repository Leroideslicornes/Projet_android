package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RejoindrePartie extends AppCompatActivity {

    private EditText editTextCode, editTextPseudo;
    private Button buttonJoin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

        // Lier les vues
        editTextCode = findViewById(R.id.editTextCode);
        editTextPseudo = findViewById(R.id.editTextPseudo);
        buttonJoin = findViewById(R.id.buttonJoin);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        buttonJoin.setOnClickListener(view -> rejoindrePartie());
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
                for (String key : documentSnapshot.getData().keySet()) {
                    if (key.startsWith("Partie_")) {
                        List<Object> partieList = (List<Object>) documentSnapshot.get(key);
                        if (partieList != null && partieList.size() > 0) {
                            Object codePartie = partieList.get(0);
                            if (codePartie != null && codePartie.toString().equals(code)) {
                                codeTrouve = true;
                                break;
                            }
                        }
                    }
                }

                if (codeTrouve) {
                    Toast.makeText(this, "Code trouvé !", Toast.LENGTH_SHORT).show();

                    // Lance l'activité Quizz avec les informations
                    Intent intent = new Intent(this, Quizz.class);
                    intent.putExtra("CODE_PARTIE", code);
                    intent.putExtra("PSEUDO", pseudo);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Code invalide, réessaye.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Le document Partie_en_cours n'existe pas.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur Firebase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
