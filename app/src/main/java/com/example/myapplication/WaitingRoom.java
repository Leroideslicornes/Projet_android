package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaitingRoom extends AppCompatActivity {

    private TextView waiting_text;
    private TextView textViewRoomCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        waiting_text = findViewById(R.id.waiting_text);
        textViewRoomCode = findViewById(R.id.textViewRoomCode);

        // Ajuster la position du second TextView pour éviter la superposition
        textViewRoomCode.setText("");  // Vider le texte par défaut

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Récupérer le roomcode depuis Firebase
        getRoomCode();
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
                            java.util.List<Object> partie1 = (java.util.List<Object>) document.get("Partie_1");
                            if (partie1 != null && !partie1.isEmpty()) {
                                // Récupérer le roomcode à l'index 0
                                String roomCode = partie1.get(0).toString();
                                // Afficher le roomcode
                                textViewRoomCode.setText("Code de la salle: " + roomCode);
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
                    textViewRoomCode.setText("Erreur: " + task.getException().getMessage());
                }
            }
        });
    }
}