package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WaitingRoomJoueur extends AppCompatActivity {

    private TextView textViewPseudo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_joueur);

        // Lier la vue
        textViewPseudo = findViewById(R.id.textViewPseudo);

        // Récupérer le pseudo envoyé
        String pseudo = getIntent().getStringExtra("PSEUDO");

        if (pseudo != null) {
            textViewPseudo.setText("Bienvenue " + pseudo + " !");
        } else {
            textViewPseudo.setText("Bienvenue !");
        }
    }
}
