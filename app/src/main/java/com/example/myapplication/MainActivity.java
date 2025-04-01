package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer les boutons par leur ID
        Button joinGameButton = findViewById(R.id.button_join_game);
        Button createGameButton = findViewById(R.id.button_create_game);

        // Définir les actions à effectuer lors des clics sur les boutons
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lorsque le bouton "Rejoindre une partie" est cliqué
                Toast.makeText(MainActivity.this, "Rejoindre une partie cliqué", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Login_user.class);
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
    }
}

