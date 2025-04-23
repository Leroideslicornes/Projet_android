package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication.databinding.ActivityQuizzBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quizz extends AppCompatActivity implements Fragment_Question.OnAnswerSelectedListener {

    private ActivityQuizzBinding binding;
    private List<Fragment_Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizzBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questions = new ArrayList<>();

        // Appel pour récupérer les questions depuis Firestore
        addQuestionsFromFirestore();

        // Bouton Restart (initialement invisible)
        binding.restart.setOnClickListener(v -> {
            currentQuestionIndex = 0;
            correctAnswers = 0;
            binding.restart.setVisibility(View.GONE);
            showNextQuestion();
        });
    }

    private void addQuestionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Quiz")
                .document("Capitales")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        for (String key : documentSnapshot.getData().keySet()) {
                            Object value = documentSnapshot.get(key);
                            if (value instanceof List) {
                                List<?> questionList = (List<?>) value;

                                if (questionList.size() >= 6) {
                                    String question = questionList.get(0).toString();
                                    String answer1 = questionList.get(1).toString();
                                    String answer2 = questionList.get(2).toString();
                                    String answer3 = questionList.get(3).toString();
                                    String answer4 = questionList.get(4).toString();
                                    String correctAnswer = questionList.get(5).toString();

                                    questions.add(Fragment_Question.newInstance(
                                            question, answer1, answer2, answer3, answer4, correctAnswer));
                                }
                            }
                        }

                        if (!questions.isEmpty()) {
                            showNextQuestion();
                        } else {
                            Toast.makeText(this, "Aucune question trouvée.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Document 'Capitales' non trouvé.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur de récupération Firestore", Toast.LENGTH_SHORT).show()
                );
    }


    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Fragment_Question q = questions.get(currentQuestionIndex);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(binding.fragmentQuestion.getId(), q);
            ft.commit();
        }
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        if (isCorrect) {
            correctAnswers++;
        }

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showNextQuestion();
        } else {
            binding.restart.setVisibility(View.VISIBLE);

            // Aller à l'écran de classement
            Intent intent = new Intent(Quizz.this, Classement.class);
            intent.putExtra("FOUND", Integer.toString(correctAnswers));
            intent.putExtra("NB_QUESTION", Integer.toString(questions.size()));
            startActivity(intent);
        }
    }
}
