package com.example.myapplication;
import android.content.Intent;
import android.widget.CheckBox;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



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

            Button loginButton = findViewById(R.id.goToLoginButton);
            loginButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });

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
