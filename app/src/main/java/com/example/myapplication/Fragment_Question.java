package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.example.myapplication.databinding.FragmentQuestionBinding;
import java.util.Locale;

public class Fragment_Question extends Fragment {

    private FragmentQuestionBinding binding;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String correctAnswer;
    private long startTimeMillis;
    private boolean isRunning = false;
    private Handler handler = new Handler();
    private Runnable chronometerRunnable;

    // Méthode pour créer une nouvelle instance avec les 4 réponses
    public static Fragment_Question newInstance(String question, String answer1, String answer2, String answer3, String answer4, String correctAnswer) {
        Fragment_Question fragment = new Fragment_Question();
        Bundle args = new Bundle();
        args.putString("QUESTION", question);
        args.putString("ANSWER1", answer1);
        args.putString("ANSWER2", answer2);
        args.putString("ANSWER3", answer3);
        args.putString("ANSWER4", answer4);
        args.putString("CORRECT_ANSWER", correctAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupération des données passées au fragment
        if (getArguments() != null) {
            question = getArguments().getString("QUESTION", "");
            answer1 = getArguments().getString("ANSWER1", "");
            answer2 = getArguments().getString("ANSWER2", "");
            answer3 = getArguments().getString("ANSWER3", "");
            answer4 = getArguments().getString("ANSWER4", "");
            correctAnswer = getArguments().getString("CORRECT_ANSWER", "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuestionBinding.inflate(inflater, container, false);

        // Affichage des données
        binding.question.setText(question);
        binding.answer1.setText(answer1);
        binding.answer2.setText(answer2);
        binding.answer3.setText(answer3);
        binding.answer4.setText(answer4);

        // Chronomètre (compte à rebours de 12 secondes)
        startChronometer();

        // Ajouter un listener sur les réponses
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isRunning) return; // Empêche double sélection
            isRunning = false;
            stopChronometer();

            RadioButton selected = getView().findViewById(checkedId);
            if (selected != null) {
                // Calcul du temps écoulé
                float elapsedSeconds = (12f - (System.currentTimeMillis() - startTimeMillis) / 1000f);
                boolean isCorrect = correctAnswer.equals(selected.getText().toString());

                // Afficher le résultat et le score
                if (getActivity() instanceof OnAnswerSelectedListener) {
                    ((OnAnswerSelectedListener) getActivity()).onAnswerSelected(isCorrect, elapsedSeconds);
                    Log.d("TAG", "Temps pris : " + elapsedSeconds);
                }
            }
        });

        return binding.getRoot();
    }

    private void startChronometer() {
        startTimeMillis = System.currentTimeMillis();
        isRunning = true;

        chronometerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                long elapsed = System.currentTimeMillis() - startTimeMillis;
                float remainingSeconds = 12f - (elapsed / 1000f);
                binding.timerText.setText(String.format(Locale.getDefault(), "Temps : %.2f s", remainingSeconds));

                if (remainingSeconds <= 0) {
                    isRunning = false;
                    stopChronometer();
                    handleTimeExpired();
                } else {
                    handler.postDelayed(this, 100); // Mise à jour du chrono toutes les 100ms
                }
            }
        };
        handler.post(chronometerRunnable);
    }

    private void stopChronometer() {
        handler.removeCallbacks(chronometerRunnable);
    }

    private void handleTimeExpired() {
        // Si le temps est écoulé, transmettre un résultat incorrect avec 0s
        if (getActivity() instanceof OnAnswerSelectedListener) {
            ((OnAnswerSelectedListener) getActivity()).onAnswerSelected(false, 0f);
        }
    }

    // Interface pour la communication avec l'activité principale
    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect, float timeTakenSeconds);
    }
}
