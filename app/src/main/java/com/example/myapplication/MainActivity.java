package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d("Firebase", "Connecté avec UID : " + user.getUid());
            } else {
                Log.e("Firebase", "Erreur d'authentification", task.getException());
            }
        });
        // Récupérer les boutons par leur ID
        Button joinGameButton = findViewById(R.id.button_join_game);
        Button createGameButton = findViewById(R.id.button_create_game);
        Button Debug_Pierre = findViewById(R.id.button_debug_Pierre);
        Button Debug_Alexis = findViewById(R.id.button_debug_Alexis);


        // Définir les actions à effectuer lors des clics sur les boutons
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lorsque le bouton "Rejoindre une partie" est cliqué
                Toast.makeText(MainActivity.this, "Rejoindre une partie cliqué", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RejoindrePartie.class);
                startActivity(intent);
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer LoginActivity lorsque le bouton "Créer une partie" est cliqué
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        Debug_Pierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer LoginActivity lorsque le bouton "Créer une partie" est cliqué
                Intent intent = new Intent(MainActivity.this, Classement.class);
                startActivity(intent);
            }
        });

        Debug_Alexis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer LoginActivity lorsque le bouton "Créer une partie" est cliqué
                Intent intent = new Intent(MainActivity.this, Quizz.class);
                startActivity(intent);
            }
        });
    }
}

