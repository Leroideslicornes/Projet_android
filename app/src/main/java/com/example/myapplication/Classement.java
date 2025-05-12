package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Classement extends AppCompatActivity {

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        // Récupérer les scores du quiz depuis l’intent
        Intent intent = getIntent();
        int userScore = Integer.parseInt(intent.getStringExtra("FOUND"));
        int nbQuestions = Integer.parseInt(intent.getStringExtra("NB_QUESTION"));

        // Exemple de joueurs fictifs
        List<User> users = new ArrayList<>();
        users.add(new User("Alice", 7));
        users.add(new User("Bob", 5));
        users.add(new User("Charlie", 6));

        // Ajouter l’utilisateur actuel
        users.add(new User("Toi", userScore));

        // Trier les utilisateurs par score décroissant
        Collections.sort(users, Comparator.comparingInt(user -> -user.score));

        // Lier les TextView
        TextView rank1 = findViewById(R.id.firstPlaceName);
        TextView rank2 = findViewById(R.id.secondPlaceName);
        TextView rank3 = findViewById(R.id.thirdPlaceName);
        TextView score1 = findViewById(R.id.firstPlaceScore);
        TextView score2 = findViewById(R.id.secondPlaceScore);
        TextView score3 = findViewById(R.id.thirdPlaceScore);

        // Sécuriser l'affichage même si la liste est plus courte
        if (users.size() > 0) {
            rank1.setText(users.get(0).name);
            score1.setText(users.get(0).score + " / " + nbQuestions + " points");
        }
        if (users.size() > 1) {
            rank2.setText(users.get(1).name);
            score2.setText(users.get(1).score + " / " + nbQuestions + " points");
        }
        if (users.size() > 2) {
            rank3.setText(users.get(2).name);
            score3.setText(users.get(2).score + " / " + nbQuestions + " points");
        }

        // Gestion du bouton retour
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(Classement.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    // Classe interne représentant un utilisateur
    static class User {
        String name;
        int score;

        User(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
