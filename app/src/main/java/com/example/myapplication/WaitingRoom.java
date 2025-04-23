// WaitingRoomActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WaitingRoom extends AppCompatActivity {

    private TextView statusTextView;
    private TextView countdownTextView;
    private ProgressBar progressBar;
    private Button cancelButton;
    private CountDownTimer countDownTimer;
    private boolean isWaiting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // Initialiser les vues
        statusTextView = findViewById(R.id.status_text);
        countdownTextView = findViewById(R.id.countdown_text);
        progressBar = findViewById(R.id.progressBar);
        cancelButton = findViewById(R.id.cancel_button);

        // Configurer le bouton d'annulation
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                isWaiting = false;
                finish(); // Terminer l'activité
            }
        });

        // Démarrer la simulation de salle d'attente
        startWaitingProcess();
    }

    private void startWaitingProcess() {
        // Afficher le message de statut
        statusTextView.setText("En attente d'une place disponible...");
        progressBar.setVisibility(View.VISIBLE);

        // Simuler un compte à rebours de 30 secondes (30000 ms)
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Mettre à jour le texte du compte à rebours
                countdownTextView.setText("Position estimée dans la file: " + (millisUntilFinished / 1000) + " s");
            }

            @Override
            public void onFinish() {
                if (isWaiting) {
                    // Quand le compte à rebours est terminé, simuler l'accès
                    statusTextView.setText("Accès accordé!");
                    countdownTextView.setText("Redirection en cours...");
                    progressBar.setVisibility(View.GONE);

                    // Simuler une redirection après 2 secondes
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isWaiting) {
                                // Code pour rediriger vers l'écran principal
                                // Intent intent = new Intent(WaitingRoomActivity.this, MainActivity.class);
                                // startActivity(intent);
                                finish();
                            }
                        }
                    }, 2000);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Annuler le compte à rebours si l'activité est détruite
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isWaiting = false;
    }
}
