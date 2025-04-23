package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class Lobby extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        TextView textViewRole = findViewById(R.id.textViewRole);

        // Récupérer le rôle passé depuis MainActivity
        String userRole = getIntent().getStringExtra("userRole");

        if (userRole != null) {
            if (userRole.equals("ADMIN")) {
                textViewRole.setText("Bienvenue Admin !");
                // Afficher les options Admin
            } else {
                textViewRole.setText("Bienvenue Joueur !");
                // Afficher les options Joueur
            }
        }
    }
}
