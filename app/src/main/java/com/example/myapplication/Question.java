package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Question extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Exemple de texte statique pour la question et les réponses
        String questionText = "Quelle est la capitale de la France ?";
        String option1 = "Paris";
        String option2 = "Londres";
        String option3 = "Berlin";
        String option4 = "Madrid";

        // Mettre à jour les TextView avec les valeurs
        TextView questionTextView = findViewById(R.id.question_text);
        TextView option1TextView = findViewById(R.id.option1_text);
        TextView option2TextView = findViewById(R.id.option2_text);
        TextView option3TextView = findViewById(R.id.option3_text);
        TextView option4TextView = findViewById(R.id.option4_text);

        questionTextView.setText(questionText);
        option1TextView.setText(option1);
        option2TextView.setText(option2);
        option3TextView.setText(option3);
        option4TextView.setText(option4);
    }
}
