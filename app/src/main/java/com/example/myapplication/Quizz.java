package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication.databinding.ActivityQuizzBinding;

import java.util.ArrayList;
import java.util.List;

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
        addQuestions();
        showNextQuestion();

        // Bouton Restart (initialement invisible)
        binding.restart.setOnClickListener(v -> {
            currentQuestionIndex = 0;
            correctAnswers = 0;
            binding.restart.setVisibility(View.GONE);
            showNextQuestion();
        });
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
            // Aller à l'écran de classement
            Intent intent = new Intent(Quizz.this, Classement.class);
            intent.putExtra("FOUND", Integer.toString(correctAnswers));
            intent.putExtra("NB_QUESTION", Integer.toString(questions.size()));
            startActivity(intent);
        }
    }

    private void addQuestion(String question, String answer1, String answer2, String answer3, String correctAnswer) {
        questions.add(Fragment_Question.newInstance(question, answer1, answer2, answer3, correctAnswer));
    }

    private void addQuestions() {
        addQuestion("En quelle année est sorti Super Mario Kart ?", "1982", "1992", "2002", "1992");
        addQuestion("En quelle année est sorti The Legend of Zelda: Spirit Tracks ?", "2005", "2007", "2009", "2009");
        addQuestion("Combien existe-t-il de versions de GTA ?", "5", "6", "12", "5");
    }
}
