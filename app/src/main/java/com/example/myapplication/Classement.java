package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Classement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classement);

        // Données des utilisateurs
        List<User> users = new ArrayList<>();
        users.add(new User("Alice", 85));
        users.add(new User("Bob", 23));
        users.add(new User("Charlie", 75));

        // Trier les utilisateurs par score décroissant
        Collections.sort(users, Comparator.comparingInt(user -> -user.score));

        // Mettre à jour les TextView avec les informations de classement
        TextView rank1 = findViewById(R.id.firstPlaceName);
        TextView rank2 = findViewById(R.id.secondPlaceName);
        TextView rank3 = findViewById(R.id.thirdPlaceName);
        TextView score1 = findViewById(R.id.firstPlaceScore);
        TextView score2 = findViewById(R.id.secondPlaceScore);
        TextView score3 = findViewById(R.id.thirdPlaceScore);

        rank1.setText(users.get(0).name);
        rank2.setText(users.get(1).name);
        rank3.setText(users.get(2).name);
        String score = String.valueOf(users.get(0).score) + " points";
        score1.setText(score);
        /*score2.setText(users.get(1).score);
        score3.setText(users.get(2).score);*/
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
