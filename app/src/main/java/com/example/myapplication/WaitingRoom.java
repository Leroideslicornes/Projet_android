package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WaitingRoom extends AppCompatActivity {

    private TextView waiting_text;
    private TextView textViewRoomCode;
    private EditText editTextNumberOfQuestions;
    private Button buttonConfirmNumber;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // Lier les vues
        waiting_text = findViewById(R.id.waiting_text);
        textViewRoomCode = findViewById(R.id.textViewRoomCode);
        editTextNumberOfQuestions = findViewById(R.id.editTextNumberOfQuestions);
        buttonConfirmNumber = findViewById(R.id.buttonConfirmNumber);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Récupérer et afficher le code de la salle
        getRoomCode();

        // Action quand on clique sur "Valider"
        buttonConfirmNumber.setOnClickListener(view -> {
            String numberText = editTextNumberOfQuestions.getText().toString().trim();
            if (!numberText.isEmpty()) {
                try {
                    int numberOfQuestions = Integer.parseInt(numberText);
                    updateNumberOfQuestions(numberOfQuestions);
                } catch (NumberFormatException e) {
                    Toast.makeText(WaitingRoom.this, "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(WaitingRoom.this, "Veuillez remplir le champ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour mettre à jour le nombre de questions dans Firestore
    private void updateNumberOfQuestions(int numberOfQuestions) {
        DocumentReference partiesRef = db.collection("Partie").document("Partie_en_cours");
        partiesRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer le tableau "Partie_1" du document
                        List<Object> partieList = (List<Object>) documentSnapshot.get("Partie_1");
                        if (partieList != null) {
                            // Met à jour ou ajoute les valeurs
                            if (partieList.size() > 0) {
                                partieList.set(2, numberOfQuestions);
                                partieList.set(1, "Partie en cours");
                            } else {
                                partieList.add(2, numberOfQuestions);
                                partieList.add(1, "Partie en cours");
                            }

                            // Mettre à jour Firestore avec les nouvelles données
                            partiesRef.update("Partie_1", partieList)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(WaitingRoom.this, "Nombre de questions : " + numberOfQuestions, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(WaitingRoom.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WaitingRoom.this, "Erreur de récupération des données", Toast.LENGTH_SHORT).show();
                });
    }

    private void getRoomCode() {
        DocumentReference docRef = db.collection("Partie").document("Partie_en_cours");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            // Récupérer le tableau Partie_1
                            List<Object> partie1 = (List<Object>) document.get("Partie_1");
                            if (partie1 != null && !partie1.isEmpty()) {
                                // Récupérer le roomcode à l'index 0
                                String roomCode = partie1.get(0).toString();
                                // Afficher le roomcode
                                textViewRoomCode.setText("Code de la salle : " + roomCode);
                            } else {
                                textViewRoomCode.setText("Pas de code disponible");
                            }
                        } catch (Exception e) {
                            textViewRoomCode.setText("Erreur lors de la récupération du code");
                            e.printStackTrace();
                        }
                    } else {
                        textViewRoomCode.setText("Document non trouvé");
                    }
                } else {
                    textViewRoomCode.setText("Erreur : " + task.getException().getMessage());
                }
            }
        });
    }
}
