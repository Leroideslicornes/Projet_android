package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication.databinding.ActivityQuizzBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

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
        addQuestionsFromFirestore("all");

        // Bouton Restart (initialement invisible)
        binding.restart.setOnClickListener(v -> {
            currentQuestionIndex = 0;
            correctAnswers = 0;
            binding.restart.setVisibility(View.GONE);
            showNextQuestion();
        });
    }

    private void addQuestionsFromFirestore(String themeName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        questions.clear(); // Réinitialise la liste de questions
        Set<String> seenQuestions = new HashSet<>(); // Pour éviter les doublons

        if (themeName.equalsIgnoreCase("all")) {
            // Récupère tous les documents (thèmes) de la collection "Quiz"
            db.collection("Quiz")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> data = document.getData();

                            for (String key : data.keySet()) {
                                Object value = data.get(key);
                                if (value instanceof List) {
                                    List<?> questionList = (List<?>) value;

                                    if (questionList.size() >= 6) {
                                        String question = questionList.get(0).toString();

                                        if (!seenQuestions.contains(question)) {
                                            seenQuestions.add(question);

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
                            }
                        }

                        if (!questions.isEmpty()) {
                            Collections.shuffle(questions);
                            showNextQuestion();
                        } else {
                            Toast.makeText(this, "Aucune question trouvée dans tous les thèmes.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur de récupération (mode all)", Toast.LENGTH_SHORT).show()
                    );

        } else {
            // Récupère les questions d’un seul thème
            db.collection("Quiz")
                    .document(themeName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            for (String key : data.keySet()) {
                                Object value = data.get(key);
                                if (value instanceof List) {
                                    List<?> questionList = (List<?>) value;

                                    if (questionList.size() >= 6) {
                                        String question = questionList.get(0).toString();

                                        if (!seenQuestions.contains(question)) {
                                            seenQuestions.add(question);

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
                            }

                            if (!questions.isEmpty()) {
                                Collections.shuffle(questions);
                                showNextQuestion();
                            } else {
                                Toast.makeText(this, "Aucune question trouvée dans ce thème.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Thème '" + themeName + "' non trouvé.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur de récupération du thème " + themeName, Toast.LENGTH_SHORT).show()
                    );
        }
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
}
