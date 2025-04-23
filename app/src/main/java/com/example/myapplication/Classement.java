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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classement);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Données des utilisateurs
        List<User> users = new ArrayList<>();
        users.add(new User("Alice", 85));
        users.add(new User("Bob", 23));
        users.add(new User("Charlie", 75));

        // Trier les utilisateurs par score décroissant
        Collections.sort(users, Comparator.comparingInt(user -> -user.score));

        // Mettre à jour les TextView avec les informations de classement
        TextView rank1 = findViewById(R.id.textView3);
        TextView rank2 = findViewById(R.id.textView4);
        TextView rank3 = findViewById(R.id.textView5);
        TextView name1 = findViewById(R.id.textView6);
        TextView name2 = findViewById(R.id.textView7);
        TextView name3 = findViewById(R.id.textView8);

        rank1.setText("1");
        rank2.setText("2");
        rank3.setText("3");
        name1.setText(users.get(0).name + " - " + users.get(0).score);
        name2.setText(users.get(1).name + " - " + users.get(1).score);
        name3.setText(users.get(2).name + " - " + users.get(2).score);
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
