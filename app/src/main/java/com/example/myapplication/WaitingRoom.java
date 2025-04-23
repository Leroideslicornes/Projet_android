package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WaitingRoom extends AppCompatActivity {

    private TextView waiting_text;
    private TextView textViewRoomCode;
    private EditText editTextNumberOfQuestions;
    private Button buttonConfirmNumber;
    private FirebaseFirestore db;
    private Spinner spinnerQuiz;
    private ArrayAdapter<String> adapterQuiz;
    private List<String> quizList = new ArrayList<>();

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
        spinnerQuiz = findViewById(R.id.spinnerQuiz);

        listenNombreJoueursTempsReel();

        // Récupérer et afficher le code de la salle
        getRoomCode();
        setupSpinnerQuiz();

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
        private void setupSpinnerQuiz() {
            adapterQuiz = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quizList);
            adapterQuiz.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerQuiz.setAdapter(adapterQuiz);

            db.collection("Quiz").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            quizList.add(doc.getId()); // Récupère le nom du document
                        }
                        adapterQuiz.notifyDataSetChanged(); // Met à jour le menu
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur de chargement : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            spinnerQuiz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String quizChoisi = quizList.get(position);
                    Toast.makeText(getApplicationContext(), "Quiz sélectionné : " + quizChoisi, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Rien à faire
                }
            });
    }

    private void listenNombreJoueursTempsReel() {
        DocumentReference partieRef = db.collection("Partie").document("Partie_en_cours");

        partieRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<String> joueurs = (List<String>) documentSnapshot.get("Partie1_players");
                int nombreJoueurs = joueurs != null ? joueurs.size() : 0;

                TextView textViewNombreJoueurs = findViewById(R.id.textViewNombreJoueurs);
                textViewNombreJoueurs.setText("Nombre de joueurs : " + nombreJoueurs);
            }
        });
    }
}
