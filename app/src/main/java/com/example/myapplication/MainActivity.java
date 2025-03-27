package com.example.myapplication;
import android.widget.CheckBox;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = findViewById(R.id.checkBox);
        // Initialiser les vues
        textView = findViewById(R.id.textView);
        imageButton1 = findViewById(R.id.imageButton2);
        imageButton2 = findViewById(R.id.imageButton3);
        imageButton3 = findViewById(R.id.imageButton4);

        // Configurer l'écouteur d'événements pour le ImageButton
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    textView.setText("Ordinateur portable");
                } else {
                    textView.setText("Portable Computer");
                }
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    textView.setText("Masque de realiter virtuelle");
                } else {
                    textView.setText("Virtual reality masque");
                }
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    textView.setText("manette");
                } else {
                    textView.setText("Controller");
                }
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("...");
            }
        });
    }
}

