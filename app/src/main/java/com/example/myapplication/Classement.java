package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Classement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        // Données des utilisateurs (exemple)
        List<User> users = new ArrayList<>();
        users.add(new User("Alice", 85));
        users.add(new User("Bob", 23));
        users.add(new User("Charlie", 75));

        // Trier les utilisateurs par score décroissant
        Collections.sort(users, Comparator.comparingInt(user -> -user.score));

        // Lier les TextView
        TextView rank1 = findViewById(R.id.firstPlaceName);
        TextView rank2 = findViewById(R.id.secondPlaceName);
        TextView rank3 = findViewById(R.id.thirdPlaceName);
        TextView score1 = findViewById(R.id.firstPlaceScore);
        TextView score2 = findViewById(R.id.secondPlaceScore);
        TextView score3 = findViewById(R.id.thirdPlaceScore);

        // Mettre à jour les TextView
        rank1.setText(users.get(0).name);
        rank2.setText(users.get(1).name);
        rank3.setText(users.get(2).name);

        score1.setText(users.get(0).score + " points");
        score2.setText(users.get(1).score + " points");
        score3.setText(users.get(2).score + " points");

        // Gestion du bouton retour
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    // Classe pour représenter un utilisateur
    static class User {
        String name;
        int score;

        User(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
